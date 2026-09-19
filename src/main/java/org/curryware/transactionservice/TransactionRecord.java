package org.curryware.transactionservice;

public class TransactionRecord {
    private String playerName;
    private String playerTeam;
    private String playerStatus;
    private String transactionType;
    private String destinationTeam;

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(String playerTeam) { this.playerTeam = playerTeam; }

    public String getPlayerStatus() { return playerStatus; }
    public void setPlayerStatus(String playerStatus) { this.playerStatus = playerStatus; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getDestinationTeam() { return destinationTeam; }
    public void setDestinationTeam(String destinationTeam) { this.destinationTeam = destinationTeam; }
}
