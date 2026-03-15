package org.curryware.currywarejavaapiserver;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class TestDatabaseConfig {

    @Bean
    public DataSource dataSource() {
        return Mockito.mock(DataSource.class);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return Mockito.mock(JdbcTemplate.class);
    }
}
