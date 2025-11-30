package org.curryware.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PostgresRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getPlayerNames() {
        String sql = "SELECT player_name FROM players";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getGameInfo() {
        String sql = "SELECT * FROM game_league_info";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTransactionInfo() {
        String sql = """
                SELECT tp.transaction_key, tp.destination_team_id, tp.player_key, player_name, ti.team_name,
                       tri.transaction_id
                FROM transaction_player tp
                JOIN player_info p ON tp.player_key = p.player_season_key
                JOIN team_info ti ON tp.destination_team_id = ti.team_key
                JOIN transaction_info tri ON tp.transaction_key = tri.transaction_key\s
                WHERE tp.destination_team = 'waivers'
                ORDER BY tri.transaction_id DESC""";
        return jdbcTemplate.queryForList(sql);
    }
}
