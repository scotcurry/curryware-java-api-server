package org.curryware.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PostgresConfig {

    private static final Logger logger = LogManager.getLogger(PostgresConfig.class);

    @Bean
    public DataSource dataSource() {

        var configValidator = new DatabaseConfigValidator();
        var postgresPassword = configValidator.getPassword();
        var postgresUsername = configValidator.getUsername();
        var postgresServer = configValidator.getServer();
        var postgresPort = configValidator.getPort();
        var postgresDatabase = configValidator.getDatabase();
        var connectionString = String.format("jdbc:postgresql://%s:%s/%s", postgresServer, postgresPort, postgresDatabase);

        var networkCheck = configValidator.checkNetworkConnectivity(postgresServer, postgresPort);
        if (!networkCheck) {
            if (logger.isErrorEnabled()) {
                logger.error("Cannot connect to Postgres server at {}:{}", postgresServer, postgresPort);
            }
            return null;
        }

        try {
            if (postgresPassword == null || postgresPassword.isEmpty()) {
                logger.error("POSTGRES_PASSWORD environment variable not set");
                // throw new RuntimeException("POSTGRES_PASSWORD environment variable not set");
            }
            return DataSourceBuilder.create()
                    .url(connectionString)
                    .username(postgresUsername)
                    .password(postgresPassword)
                    .build();
        } catch (Exception e) {
            logger.error("Error creating DataSource", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
