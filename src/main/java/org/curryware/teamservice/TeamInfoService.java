package org.curryware.teamservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamInfoService {

    private static final Logger logger = LogManager.getLogger(TeamInfoService.class);
    private final JdbcTemplate jdbcTemplate;

    public TeamInfoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TeamInfoRecord> getTeamsByLeague(String gameId, String leagueId) {
        String leagueKey = gameId + ".l." + leagueId;
        String sql = "SELECT league_key, team_key, team_id, team_name, team_logo, " +
                     "previous_season_team_rank, number_of_moves, number_of_trades, " +
                     "draft_position, draft_grade, manager_nicknames " +
                     "FROM all_team_information WHERE league_key = ? ORDER BY team_id";
        List<TeamInfoRecord> records = jdbcTemplate.query(sql, teamRowMapper(), leagueKey);
        logger.info("Retrieved {} teams for league_key={}", records.size(), leagueKey);
        return records;
    }

    private RowMapper<TeamInfoRecord> teamRowMapper() {
        return (rs, rowNum) -> {
            TeamInfoRecord record = new TeamInfoRecord();
            record.setLeagueKey(rs.getString("league_key"));
            record.setTeamKey(rs.getString("team_key"));
            record.setTeamId(rs.getInt("team_id"));
            record.setTeamName(rs.getString("team_name"));
            record.setTeamLogo(rs.getString("team_logo"));
            record.setPreviousSeasonTeamRank((Integer) rs.getObject("previous_season_team_rank"));
            record.setNumberOfMoves((Integer) rs.getObject("number_of_moves"));
            record.setNumberOfTrades((Integer) rs.getObject("number_of_trades"));
            record.setDraftPosition((Integer) rs.getObject("draft_position"));
            record.setDraftGrade(rs.getString("draft_grade"));
            record.setManagerNicknames(rs.getString("manager_nicknames"));
            return record;
        };
    }
}
