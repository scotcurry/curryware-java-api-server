package org.curryware.userservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    private static final Logger logger = LogManager.getLogger(UserInfoService.class);
    private static final int DEFAULT_USER_ID = 1;
    private static final String DEFAULT_USER_NAME = "scotcurry4@gmail.com";

    private final JdbcTemplate jdbcTemplate;

    public UserInfoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean saveApnsToken(String apnsToken) {
        String sql = "INSERT INTO user_info (user_id, user_name, apns_token) VALUES (?, ?, ?) " +
                     "ON CONFLICT (user_id) DO UPDATE SET apns_token = EXCLUDED.apns_token";
        try {
            int rowsAffected = jdbcTemplate.update(sql, DEFAULT_USER_ID, DEFAULT_USER_NAME, apnsToken);
            logger.info("Saved apns_token for user_id={}, rowsAffected={}", DEFAULT_USER_ID, rowsAffected);
            return rowsAffected > 0;
        } catch (DataAccessException ex) {
            logger.error("Failed to save apns_token for user_id={}: {}", DEFAULT_USER_ID, ex.getMessage());
            return false;
        }
    }
}
