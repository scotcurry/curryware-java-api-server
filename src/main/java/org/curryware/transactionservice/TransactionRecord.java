package org.curryware.transactionservice;

public class TransactionRecord {

    private String transaction_key;
    private String destination_team_id;
    private String player_key;
    private String player_name;
    private String team_name;
    private Integer transaction_id;

    public String getTransaction_key() {
        return transaction_key;
    }

    public String getDestination_team_id() {
        return destination_team_id;
    }

    public String getPlayer_key() {
        return player_key;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public String getTeam_name() {
        return team_name;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_key(String transaction_key) {
        this.transaction_key = transaction_key;
    }

    public void setDestination_team_id(String destination_team_id) {
        this.destination_team_id = destination_team_id;
    }

    public void setPlayer_key(String player_key) {
        this.player_key = player_key;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setTransaction_id(Integer transaction_id) {
        this.transaction_id = transaction_id;
    }
}
