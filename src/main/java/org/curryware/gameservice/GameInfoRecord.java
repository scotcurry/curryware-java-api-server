package org.curryware.gameservice;

public class GameInfoRecord {
    private String league_id;
    private String game_id;
    private String team_name;

    public String getLeague_id() {
        return league_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setLeague_id(String league_id) {
        this.league_id = league_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
}