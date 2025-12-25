package org.curryware.transactionservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.controllers.GameController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionService.class);
    private final JdbcTemplate jdbcTemplate;

    public TransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ... existing code ...
    public List<TransactionRecord> getTransactions(int gameId, int leagueId) {
        String sql = """
                SELECT tp.transaction_key, tp.destination_team_id, tp.player_key, player_name, ti.team_name,
                                                    tri.transaction_id, extract(EPOCH FROM tri.transaction_time) AS transaction_time,
                                                    p.player_team, p.player_headshot, p.player_name, tri.game_id, tri.league_id
                                             FROM transaction_player tp
                                                      JOIN player_info p ON tp.player_key = p.player_season_key
                                                      JOIN team_info ti ON tp.destination_team_id = ti.team_key
                                                      JOIN transaction_info tri ON tp.transaction_key = tri.transaction_key
                                             WHERE tp.destination_team = 'waivers'
                                             AND tri.game_id = ? AND tri.league_id = ?
                                             ORDER BY tri.transaction_id DESC""";
        List<TransactionRecord> transactionRecords = jdbcTemplate.query(sql, transactionRecordRowMapper(), gameId, leagueId);
        int totalGames = transactionRecords.size();
        logger.info("Retrieved {} transactions for gameId: {} and leagueId: {}", totalGames, gameId, leagueId);
        return transactionRecords;
    }

    private RowMapper<TransactionRecord> transactionRecordRowMapper() {

        return (rs, rowNum) -> {
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setTransaction_key(rs.getString("transaction_key"));
            transactionRecord.setDestination_team_id(rs.getString("destination_team_id"));
            transactionRecord.setPlayer_key(rs.getString("player_key"));
            transactionRecord.setTeam_name(rs.getString("team_name"));
            transactionRecord.setTransaction_id(rs.getInt("transaction_id"));
            transactionRecord.setTransaction_time(rs.getLong("transaction_time"));
            transactionRecord.setPlayer_team(rs.getString("player_team"));
            transactionRecord.setPlayer_headshot(rs.getString("player_headshot"));
            transactionRecord.setPlayer_name(rs.getString("player_name"));
            transactionRecord.setGame_id(rs.getInt("game_id"));
            transactionRecord.setLeague_id(rs.getInt("league_id"));
            return transactionRecord;
        };
    }
}
