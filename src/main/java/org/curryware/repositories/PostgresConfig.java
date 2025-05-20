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
        try {
            String postgresPassword = System.getenv("POSTGRES_PASSWORD");
            if (postgresPassword == null || postgresPassword.isEmpty()) {
                logger.error("POSTGRES_PASSWORD environment variable not set");
                // throw new RuntimeException("POSTGRES_PASSWORD environment variable not set");
            }
            return DataSourceBuilder.create()
                    .url("jdbc:postgresql://postgres.curryware.org:5432/currywarefantasy")
                    .username("scot")
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
