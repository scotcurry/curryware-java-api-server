package org.curryware.gameservice;

public class GameInfoRecord {
    private String leagueKey;
    private Integer leagueId;
    private Integer gameId;
    private String leagueName;
    private String leagueLogoUrl;
    private Integer numberOfTeams;
    private String leagueUpdateTimestamp;
    private String startDate;
    private String endWeek;
    private Integer season;

    public String getLeagueKey() { return leagueKey; }
    public void setLeagueKey(String leagueKey) { this.leagueKey = leagueKey; }

    public Integer getLeagueId() { return leagueId; }
    public void setLeagueId(Integer leagueId) { this.leagueId = leagueId; }

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public String getLeagueName() { return leagueName; }
    public void setLeagueName(String leagueName) { this.leagueName = leagueName; }

    public String getLeagueLogoUrl() { return leagueLogoUrl; }
    public void setLeagueLogoUrl(String leagueLogoUrl) { this.leagueLogoUrl = leagueLogoUrl; }

    public Integer getNumberOfTeams() { return numberOfTeams; }
    public void setNumberOfTeams(Integer numberOfTeams) { this.numberOfTeams = numberOfTeams; }

    public String getLeagueUpdateTimestamp() { return leagueUpdateTimestamp; }
    public void setLeagueUpdateTimestamp(String leagueUpdateTimestamp) { this.leagueUpdateTimestamp = leagueUpdateTimestamp; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndWeek() { return endWeek; }
    public void setEndWeek(String endWeek) { this.endWeek = endWeek; }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
}
