package org.curryware.playerservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Caches each league's available-player list, because fetching it takes about 17 seconds.
 *
 * - The first request for a league waits for the fetch.
 * - Within the TTL, requests use the cached list.
 * - After the TTL, requests still get the cached list at once while a background fetch
 *   replaces it (stale-while-revalidate). If that fetch fails, the old list stays.
 * - Concurrent requests for the same league share one fetch.
 */
@Component
public class AvailablePlayersCache {

    private static final Logger logger = LogManager.getLogger(AvailablePlayersCache.class);

    private final AvailablePlayersClient client;
    private final Duration ttl;
    private final Clock clock;
    private final Executor refreshExecutor;
    private final ConcurrentMap<String, AvailablePlayersSnapshot> snapshots = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompletableFuture<AvailablePlayersSnapshot>> inFlight =
            new ConcurrentHashMap<>();

    @Autowired
    public AvailablePlayersCache(AvailablePlayersClient client,
                                 @Value("${curryware.available-players.cache-ttl:PT15M}") Duration ttl) {
        this(client, ttl, Clock.systemUTC(), Executors.newFixedThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable, "available-players-refresh");
            thread.setDaemon(true);
            return thread;
        }));
    }

    AvailablePlayersCache(AvailablePlayersClient client, Duration ttl, Clock clock, Executor refreshExecutor) {
        this.client = client;
        this.ttl = ttl;
        this.clock = clock;
        this.refreshExecutor = refreshExecutor;
    }

    public AvailablePlayersSnapshot get(int gameId, int leagueId) {
        String key = gameId + ".l." + leagueId;
        AvailablePlayersSnapshot cached = snapshots.get(key);
        if (cached != null) {
            if (Duration.between(cached.getFetchedAt(), clock.instant()).compareTo(ttl) > 0) {
                refresh(key, gameId, leagueId).exceptionally(ex -> {
                    logger.warn("Background refresh of available players for {} failed; keeping the list from {}",
                            key, cached.getFetchedAt(), ex);
                    return null;
                });
            }
            return cached;
        }
        try {
            return refresh(key, gameId, leagueId).join();
        } catch (CompletionException ex) {
            throw new AvailablePlayersUnavailableException(
                    "Couldn't fetch available players for league " + key, ex.getCause());
        }
    }

    private CompletableFuture<AvailablePlayersSnapshot> refresh(String key, int gameId, int leagueId) {
        // Register the future before starting the fetch, and remove it before completing it, so a
        // fetch that fails instantly can't leave a finished future behind that blocks later fetches.
        CompletableFuture<AvailablePlayersSnapshot> fetch = new CompletableFuture<>();
        CompletableFuture<AvailablePlayersSnapshot> existing = inFlight.putIfAbsent(key, fetch);
        if (existing != null) {
            return existing;
        }
        try {
            refreshExecutor.execute(() -> {
                try {
                    Instant fetchedAt = clock.instant();
                    AvailablePlayersSnapshot snapshot =
                            new AvailablePlayersSnapshot(client.fetchAvailablePlayerIds(gameId, leagueId), fetchedAt);
                    snapshots.put(key, snapshot);
                    inFlight.remove(key, fetch);
                    fetch.complete(snapshot);
                } catch (RuntimeException ex) {
                    inFlight.remove(key, fetch);
                    fetch.completeExceptionally(ex);
                }
            });
        } catch (RejectedExecutionException ex) {
            inFlight.remove(key, fetch);
            fetch.completeExceptionally(ex);
        }
        return fetch;
    }
}
