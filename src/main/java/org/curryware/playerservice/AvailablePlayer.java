package org.curryware.playerservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** One player from the available-players Cloud Function: {"full_name": "...", "player_id": "40881"}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailablePlayer {
    @JsonProperty("full_name")
    private String fullName;

    /** Yahoo player id. The Cloud Function sends it as a string. */
    @JsonProperty("player_id")
    private String playerId;

    public AvailablePlayer() {
    }

    public AvailablePlayer(String fullName, String playerId) {
        this.fullName = fullName;
        this.playerId = playerId;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
}
