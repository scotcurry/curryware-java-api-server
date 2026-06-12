package org.curryware.gameservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameInfoService {

    private static final Logger logger = LogManager.getLogger(GameInfoService.class);
    private final JdbcTemplate jdbcTemplate;

    public GameInfoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GameInfoRecord> getAllLeagues() {
        String sql = "SELECT league_key, league_id, game_id, league_name, league_logo_url, number_of_teams, " +
                     "league_update_timestamp, start_date, end_week, season " +
                     "FROM all_league_information ORDER BY season DESC, league_id";
        List<GameInfoRecord> records = jdbcTemplate.query(sql, leagueRowMapper());
        logger.info("Retrieved {} leagues from all_league_information", records.size());
        return records;
    }

    private RowMapper<GameInfoRecord> leagueRowMapper() {
        return (rs, rowNum) -> {
            GameInfoRecord record = new GameInfoRecord();
            record.setLeagueKey(rs.getString("league_key"));
            record.setLeagueId(rs.getInt("league_id"));
            record.setGameId(rs.getInt("game_id"));
            record.setLeagueName(rs.getString("league_name"));
            record.setLeagueLogoUrl(rs.getString("league_logo_url"));
            record.setNumberOfTeams(rs.getInt("number_of_teams"));
            var ts = rs.getTimestamp("league_update_timestamp");
            record.setLeagueUpdateTimestamp(ts != null ? ts.toInstant().toString() : null);
            record.setStartDate(rs.getString("start_date"));
            record.setEndWeek(rs.getString("end_week"));
            record.setSeason(rs.getInt("season"));
            return record;
        };
    }
}
