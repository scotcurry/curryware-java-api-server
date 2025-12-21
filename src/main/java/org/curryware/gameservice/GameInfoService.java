package org.curryware.gameservice;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameInfoService {

    private final JdbcTemplate jdbcTemplate;

    public GameInfoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GameInfoRecord> getCurrentGames() {
        String sql = "SELECT * FROM game_league_info WHERE game_year = 2025";
        List<GameInfoRecord> gameInfoRecords = jdbcTemplate.query(sql, gameLeagueInfoRowmapper());
        int totalGames = gameInfoRecords.size();
        System.out.println(totalGames);
        return gameInfoRecords;
    }

    private RowMapper<GameInfoRecord> gameLeagueInfoRowmapper() {
        return (rs, rowNum) -> {
            GameInfoRecord gameInfoRecord = new GameInfoRecord();
            gameInfoRecord.setGame_id(String.valueOf(rs.getInt("game_id")));
            gameInfoRecord.setLeague_id(String.valueOf(rs.getString("league_id")));
            gameInfoRecord.setTeam_name(rs.getString("team_name"));
            gameInfoRecord.setPaid_league(rs.getBoolean("paid_league"));
            gameInfoRecord.setGame_year(rs.getInt("game_year"));
            gameInfoRecord.setLeague_image(rs.getString("league_image"));
            return gameInfoRecord;
        };
    }
}
