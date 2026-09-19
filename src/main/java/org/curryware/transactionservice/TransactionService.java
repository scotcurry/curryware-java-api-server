package org.curryware.transactionservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    public List<TransactionRecord> getTransactions(String leagueId, String transactionTime) {
        String sql = "SELECT pi.player_name, pi.player_team, pi.player_status, ti.transaction_type, " +
                     "tp.destination_team " +
                     "FROM transaction_info ti " +
                     "JOIN transaction_player tp ON ti.transaction_key = tp.transaction_key " +
                     "JOIN player_info pi ON tp.player_id = pi.player_id " +
                     "WHERE ti.league_id = ? AND ti.transaction_time >= to_timestamp(?) " +
                     "ORDER BY ti.transaction_time DESC";
        Long leagueIdParam = Long.parseLong(leagueId);
        Long sinceTimestamp = Long.parseLong(transactionTime);
        List<TransactionRecord> records = jdbcTemplate.query(sql, transactionRowMapper(), leagueIdParam, sinceTimestamp);
        logger.info("Retrieved {} transactions for league_id={} since transaction_time={}",
                records.size(), leagueId, transactionTime);
        return records;
    }

    private RowMapper<TransactionRecord> transactionRowMapper() {
        return (rs, rowNum) -> {
            TransactionRecord record = new TransactionRecord();
            record.setPlayerName(rs.getString("player_name"));
            record.setPlayerTeam(rs.getString("player_team"));
            record.setPlayerStatus(rs.getString("player_status"));
            record.setTransactionType(rs.getString("transaction_type"));
            record.setDestinationTeam(rs.getString("destination_team"));
            return record;
        };
    }
}
