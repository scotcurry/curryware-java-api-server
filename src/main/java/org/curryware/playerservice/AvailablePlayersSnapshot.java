package org.curryware.playerservice;

import java.time.Instant;
import java.util.List;

/** The Yahoo ids of a league's available players, and when they were fetched. */
public class AvailablePlayersSnapshot {
    private final List<Integer> yahooPlayerIds;
    private final Instant fetchedAt;

    public AvailablePlayersSnapshot(List<Integer> yahooPlayerIds, Instant fetchedAt) {
        this.yahooPlayerIds = List.copyOf(yahooPlayerIds);
        this.fetchedAt = fetchedAt;
    }

    public List<Integer> getYahooPlayerIds() { return yahooPlayerIds; }
    public Instant getFetchedAt() { return fetchedAt; }
}
