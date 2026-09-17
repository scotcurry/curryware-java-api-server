package org.curryware.userservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    private static final Logger logger = LogManager.getLogger(UserInfoService.class);

    private final JdbcTemplate jdbcTemplate;

    public UserInfoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean saveDeviceToken(String deviceToken, String deviceVendorId, String userDeviceType, String deviceName,
                                   String vendorId) {
        String sql = "INSERT INTO user_info (user_name, user_device_type, user_device_id, apns_token, vendor_id) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (user_device_id) DO UPDATE SET " +
                     "user_name = EXCLUDED.user_name, " +
                     "user_device_type = EXCLUDED.user_device_type, " +
                     "apns_token = EXCLUDED.apns_token, " +
                     "vendor_id = EXCLUDED.vendor_id";
        try {
            int rowsAffected = jdbcTemplate.update(sql, deviceName, userDeviceType, deviceVendorId, deviceToken, vendorId);
            logger.info("Saved apns_token for user_device_id={}, rowsAffected={}", deviceVendorId, rowsAffected);
            return rowsAffected > 0;
        } catch (DataAccessException ex) {
            logger.error("Failed to save apns_token for user_device_id={}: {}", deviceVendorId, "Data " +
                    "access exception");
            return false;
        }
    }
}
