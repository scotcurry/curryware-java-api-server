package org.curryware.teamservice;

public class TeamInfoRecord {
    private String leagueKey;
    private String teamKey;
    private Integer teamId;
    private String teamName;
    private String teamLogo;
    private Integer previousSeasonTeamRank;
    private Integer numberOfMoves;
    private Integer numberOfTrades;
    private Integer draftPosition;
    private String draftGrade;
    private String managerNicknames;

    public String getLeagueKey() { return leagueKey; }
    public void setLeagueKey(String leagueKey) { this.leagueKey = leagueKey; }

    public String getTeamKey() { return teamKey; }
    public void setTeamKey(String teamKey) { this.teamKey = teamKey; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamLogo() { return teamLogo; }
    public void setTeamLogo(String teamLogo) { this.teamLogo = teamLogo; }

    public Integer getPreviousSeasonTeamRank() { return previousSeasonTeamRank; }
    public void setPreviousSeasonTeamRank(Integer previousSeasonTeamRank) { this.previousSeasonTeamRank = previousSeasonTeamRank; }

    public Integer getNumberOfMoves() { return numberOfMoves; }
    public void setNumberOfMoves(Integer numberOfMoves) { this.numberOfMoves = numberOfMoves; }

    public Integer getNumberOfTrades() { return numberOfTrades; }
    public void setNumberOfTrades(Integer numberOfTrades) { this.numberOfTrades = numberOfTrades; }

    public Integer getDraftPosition() { return draftPosition; }
    public void setDraftPosition(Integer draftPosition) { this.draftPosition = draftPosition; }

    public String getDraftGrade() { return draftGrade; }
    public void setDraftGrade(String draftGrade) { this.draftGrade = draftGrade; }

    public String getManagerNicknames() { return managerNicknames; }
    public void setManagerNicknames(String managerNicknames) { this.managerNicknames = managerNicknames; }
}
