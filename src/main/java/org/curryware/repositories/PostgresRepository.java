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
}
