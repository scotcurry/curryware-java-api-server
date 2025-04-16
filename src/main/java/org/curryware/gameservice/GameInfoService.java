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
        String sql = "SELECT * FROM game_league_info";
        return jdbcTemplate.query(sql, game_league_info_rowmapper());
    }

    private RowMapper<GameInfoRecord> game_league_info_rowmapper() {
        return (rs, rowNum) -> {
            GameInfoRecord gameInfoRecord = new GameInfoRecord();
            gameInfoRecord.setGame_id(String.valueOf(rs.getInt("game_id")));
            gameInfoRecord.setLeague_id(String.valueOf(rs.getString("league_id")));
            gameInfoRecord.setTeam_name(rs.getString("team_name"));
            return gameInfoRecord;
        };
    }
}
