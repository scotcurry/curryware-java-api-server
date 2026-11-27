package org.curryware.playerservice;

/** The league, its scoring rules or its season's stats aren't in the database. Mapped to 404. */
public class LeagueDataNotFoundException extends RuntimeException {
    public LeagueDataNotFoundException(String message) {
        super(message);
    }
}
