package org.curryware.playerservice;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final JdbcTemplate jdbcTemplate;

    public PlayerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PlayerRecord> getPlayerNames() {
        String sql = "SELECT * FROM player_info";
        List<PlayerRecord> playerRecords = jdbcTemplate.query(sql, playerRecordRowMapper());
        int totalPlayers = playerRecords.size();
        System.out.println(totalPlayers);
        return playerRecords;
    }

    private RowMapper<PlayerRecord> playerRecordRowMapper() {
        return (rs, rowNum) -> {
            PlayerRecord playerRecord = new PlayerRecord();
            playerRecord.setPlayer_id(rs.getString("player_id"));
            playerRecord.setPlayer_name(rs.getString("player_name"));
            playerRecord.setPlayer_season_key(rs.getString("player_season_key"));
            playerRecord.setPlayer_url(rs.getString("player_url"));
            playerRecord.setPlayer_team(rs.getString("player_team"));
            playerRecord.setPlayer_bye_week(rs.getInt("player_bye_week"));
            playerRecord.setPlayer_uniform_number(rs.getInt("player_uniform_number"));
            playerRecord.setPlayer_position(rs.getString("player_position"));
            playerRecord.setPlayer_headshot(rs.getString("player_headshot"));
            return playerRecord;
        };
    }
}


