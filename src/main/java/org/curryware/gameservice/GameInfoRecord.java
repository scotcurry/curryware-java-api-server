package org.curryware.gameservice;

public class GameInfoRecord {
    private String league_id;
    private String game_id;
    private String team_name;
    private boolean paid_league;
    private Integer game_year;

    public String getLeague_id() {
        return league_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public boolean isPaid_league() {
        return paid_league;
    }

    public Integer getGame_year() { return game_year;}

    public void setLeague_id(String league_id) {
        this.league_id = league_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setPaid_league(boolean paid_league) {
        this.paid_league = paid_league;
    }

    public void setGame_year(Integer game_year) { this.game_year = game_year;}
}