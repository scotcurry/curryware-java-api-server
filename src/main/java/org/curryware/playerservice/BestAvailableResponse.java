package org.curryware.playerservice;

import java.time.Instant;
import java.util.List;

public class BestAvailableResponse {
    private int gameId;
    private int leagueId;
    private int season;
    /** First and last NFL week (regular season) included in the totals. */
    private int fromWeek;
    private int toWeek;
    /** When the available-player list was fetched from Yahoo (it is cached). */
    private Instant availableAsOf;
    /** Players Yahoo lists as available, including kickers, defenses and others without stats. */
    private int availablePlayerCount;
    /** Available players found in nflverse_players for the season (QB, RB, WR and TE only). */
    private int matchedPlayerCount;
    /** League scoring stat ids with a non-zero value that this calculation can't count yet. */
    private List<Integer> unscoredStatIds;
    private List<BestAvailablePlayer> players;

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public int getLeagueId() { return leagueId; }
    public void setLeagueId(int leagueId) { this.leagueId = leagueId; }

    public int getSeason() { return season; }
    public void setSeason(int season) { this.season = season; }

    public int getFromWeek() { return fromWeek; }
    public void setFromWeek(int fromWeek) { this.fromWeek = fromWeek; }

    public int getToWeek() { return toWeek; }
    public void setToWeek(int toWeek) { this.toWeek = toWeek; }

    public Instant getAvailableAsOf() { return availableAsOf; }
    public void setAvailableAsOf(Instant availableAsOf) { this.availableAsOf = availableAsOf; }

    public int getAvailablePlayerCount() { return availablePlayerCount; }
    public void setAvailablePlayerCount(int availablePlayerCount) { this.availablePlayerCount = availablePlayerCount; }

    public int getMatchedPlayerCount() { return matchedPlayerCount; }
    public void setMatchedPlayerCount(int matchedPlayerCount) { this.matchedPlayerCount = matchedPlayerCount; }

    public List<Integer> getUnscoredStatIds() { return unscoredStatIds; }
    public void setUnscoredStatIds(List<Integer> unscoredStatIds) { this.unscoredStatIds = unscoredStatIds; }

    public List<BestAvailablePlayer> getPlayers() { return players; }
    public void setPlayers(List<BestAvailablePlayer> players) { this.players = players; }
}
