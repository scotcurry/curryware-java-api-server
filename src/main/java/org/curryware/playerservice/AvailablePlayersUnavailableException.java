package org.curryware.playerservice;

/** The available-players Cloud Function failed and nothing is cached for the league. Mapped to 502. */
public class AvailablePlayersUnavailableException extends RuntimeException {
    public AvailablePlayersUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
