package org.curryware.playerservice;

import java.math.BigDecimal;

public class BestAvailablePlayer {
    private int yahooPlayerId;
    private String fullName;
    private String position;
    private String team;
    private String headshotUrl;
    private BigDecimal totalPoints;
    private int gamesPlayed;
    private BigDecimal pointsPerGame;

    public int getYahooPlayerId() { return yahooPlayerId; }
    public void setYahooPlayerId(int yahooPlayerId) { this.yahooPlayerId = yahooPlayerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getHeadshotUrl() { return headshotUrl; }
    public void setHeadshotUrl(String headshotUrl) { this.headshotUrl = headshotUrl; }

    public BigDecimal getTotalPoints() { return totalPoints; }
    public void setTotalPoints(BigDecimal totalPoints) { this.totalPoints = totalPoints; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public BigDecimal getPointsPerGame() { return pointsPerGame; }
    public void setPointsPerGame(BigDecimal pointsPerGame) { this.pointsPerGame = pointsPerGame; }
}
