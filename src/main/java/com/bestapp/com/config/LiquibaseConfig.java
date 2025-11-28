package com.bestapp.com.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@PropertySource("application.properties")
@RequiredArgsConstructor
public class LiquibaseConfig {

    private final Environment environment;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(environment.getProperty("liquibase.changelog"));
        liquibase.setDefaultSchema(environment.getProperty("liquibase.schema"));
        liquibase.setShouldRun(true);
        return liquibase;
    }

}
