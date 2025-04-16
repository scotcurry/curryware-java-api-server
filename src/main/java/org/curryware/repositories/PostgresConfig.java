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
        String postgresPassword = System.getenv("COMMON_PASSWORD");
        logger.debug("Postgres password: {}", postgresPassword);
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://postgres.curryware.org:5432/currywarefantasy")
                .username("scot")
                .password(postgresPassword)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
