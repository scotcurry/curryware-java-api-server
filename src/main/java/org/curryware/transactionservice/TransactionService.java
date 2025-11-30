package org.curryware.transactionservice;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final JdbcTemplate jdbcTemplate;

    public TransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TransactionRecord> getTransactions() {
        String sql = """
                SELECT tp.transaction_key, tp.destination_team_id, tp.player_key, player_name, ti.team_name,
                       tri.transaction_id
                FROM transaction_player tp
                JOIN player_info p ON tp.player_key = p.player_season_key
                JOIN team_info ti ON tp.destination_team_id = ti.team_key
                JOIN transaction_info tri ON tp.transaction_key = tri.transaction_key\s
                WHERE tp.destination_team = 'waivers'
                ORDER BY tri.transaction_id DESC""";
        List<TransactionRecord> transactionRecords = jdbcTemplate.query(sql, transactionRecordRowMapper());
        int totalGames = transactionRecords.size();
        System.out.println(totalGames);
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
            return transactionRecord;
        };
    }
}
