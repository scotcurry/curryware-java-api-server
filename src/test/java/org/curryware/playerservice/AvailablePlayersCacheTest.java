package org.curryware.playerservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvailablePlayersCacheTest {

    private static final Duration TTL = Duration.ofMinutes(15);

    /** A clock the test moves forward by hand. */
    static class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-11-27T12:00:00Z");

        void advance(Duration duration) { now = now.plus(duration); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    /** Runs submitted tasks only when the test says so, so background refreshes are visible. */
    static class ManualExecutor implements Executor {
        private final Queue<Runnable> tasks = new ArrayDeque<>();

        @Override public void execute(Runnable task) { tasks.add(task); }
        int pending() { return tasks.size(); }
        void runAll() { while (!tasks.isEmpty()) { tasks.poll().run(); } }
    }

    private AvailablePlayersClient client;
    private MutableClock clock;

    @BeforeEach
    void setUp() {
        client = mock(AvailablePlayersClient.class);
        clock = new MutableClock();
    }

    private AvailablePlayersCache directCache() {
        return new AvailablePlayersCache(client, TTL, clock, Runnable::run);
    }

    @Test
    void firstRequestFetchesAndLaterRequestsUseTheCache() {
        when(client.fetchAvailablePlayerIds(470, 523652)).thenReturn(List.of(1, 2, 3));
        AvailablePlayersCache cache = directCache();

        AvailablePlayersSnapshot first = cache.get(470, 523652);
        clock.advance(Duration.ofMinutes(10));
        AvailablePlayersSnapshot second = cache.get(470, 523652);

        assertEquals(List.of(1, 2, 3), first.getYahooPlayerIds());
        assertEquals(Instant.parse("2026-11-27T12:00:00Z"), first.getFetchedAt());
        assertSame(first, second);
        verify(client, times(1)).fetchAvailablePlayerIds(470, 523652);
    }

    @Test
    void leaguesAreCachedSeparately() {
        when(client.fetchAvailablePlayerIds(470, 1)).thenReturn(List.of(10));
        when(client.fetchAvailablePlayerIds(470, 2)).thenReturn(List.of(20));
        AvailablePlayersCache cache = directCache();

        assertEquals(List.of(10), cache.get(470, 1).getYahooPlayerIds());
        assertEquals(List.of(20), cache.get(470, 2).getYahooPlayerIds());
    }

    @Test
    void expiredListIsServedWhileABackgroundFetchReplacesIt() {
        ManualExecutor executor = new ManualExecutor();
        AvailablePlayersCache cache = new AvailablePlayersCache(client, TTL, clock, executor);
        when(client.fetchAvailablePlayerIds(470, 523652)).thenReturn(List.of(1), List.of(1, 2));
        // The first request has nothing cached, so it waits for the fetch.
        Thread first = new Thread(() -> cache.get(470, 523652));
        first.start();
        while (executor.pending() == 0) { Thread.onSpinWait(); }
        executor.runAll();
        joinQuietly(first);

        clock.advance(TTL.plusMinutes(1));
        AvailablePlayersSnapshot stale = cache.get(470, 523652);

        assertEquals(List.of(1), stale.getYahooPlayerIds(), "the expired list is returned at once");
        assertEquals(1, executor.pending(), "a background fetch was started");
        executor.runAll();
        AvailablePlayersSnapshot fresh = cache.get(470, 523652);
        assertEquals(List.of(1, 2), fresh.getYahooPlayerIds());
        assertEquals(clock.instant(), fresh.getFetchedAt());
    }

    @Test
    void expiredRequestsShareOneBackgroundFetch() {
        ManualExecutor executor = new ManualExecutor();
        AvailablePlayersCache cache = new AvailablePlayersCache(client, TTL, clock, executor);
        when(client.fetchAvailablePlayerIds(470, 523652)).thenReturn(List.of(1), List.of(1, 2));
        Thread first = new Thread(() -> cache.get(470, 523652));
        first.start();
        while (executor.pending() == 0) { Thread.onSpinWait(); }
        executor.runAll();
        joinQuietly(first);
        clock.advance(TTL.plusMinutes(1));

        cache.get(470, 523652);
        cache.get(470, 523652);
        cache.get(470, 523652);

        assertEquals(1, executor.pending());
    }

    @Test
    void failedBackgroundFetchKeepsTheOldListAndTriesAgainLater() {
        AvailablePlayersCache cache = directCache();
        when(client.fetchAvailablePlayerIds(470, 523652))
                .thenReturn(List.of(1))
                .thenThrow(new IllegalStateException("Cloud Function timed out"))
                .thenReturn(List.of(1, 2));
        cache.get(470, 523652);
        clock.advance(TTL.plusMinutes(1));

        // Each expired request returns the list it found and starts a refresh, so a refreshed
        // list shows up on the request after the refresh that fetched it.
        AvailablePlayersSnapshot failedRefresh = cache.get(470, 523652);
        AvailablePlayersSnapshot retriedRefresh = cache.get(470, 523652);
        AvailablePlayersSnapshot afterRetry = cache.get(470, 523652);

        assertEquals(List.of(1), failedRefresh.getYahooPlayerIds());
        assertEquals(List.of(1), retriedRefresh.getYahooPlayerIds(), "the failed refresh kept the old list");
        assertEquals(List.of(1, 2), afterRetry.getYahooPlayerIds());
    }

    @Test
    void failedFirstFetchRaisesAndDoesNotBlockTheNextAttempt() {
        AvailablePlayersCache cache = directCache();
        when(client.fetchAvailablePlayerIds(470, 523652))
                .thenThrow(new IllegalStateException("connection refused"))
                .thenReturn(List.of(7));

        AvailablePlayersUnavailableException ex =
                assertThrows(AvailablePlayersUnavailableException.class, () -> cache.get(470, 523652));
        assertTrue(ex.getMessage().contains("470.l.523652"));
        assertEquals("connection refused", ex.getCause().getMessage());
        assertEquals(List.of(7), cache.get(470, 523652).getYahooPlayerIds());
    }

    private static void joinQuietly(Thread thread) {
        try {
            thread.join(5_000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
