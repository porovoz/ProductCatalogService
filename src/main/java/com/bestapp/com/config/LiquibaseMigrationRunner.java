package com.bestapp.com.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * Runs Liquibase migrations.
 */
@Slf4j
@RequiredArgsConstructor
public class LiquibaseMigrationRunner {

    private final DatabaseConfigProperties configProperties;

    public void runMigrations(Connection connection) throws Exception {
        String changelogPath = configProperties.getProperty("liquibase.changelog");
        String liquibaseSchema = configProperties.getProperty("liquibase.schema");

        if (changelogPath == null || changelogPath.isBlank()) {
            log.info("Liquibase changelog not set → skipping migrations");
            return;
        }

        try {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            if (liquibaseSchema != null && !liquibaseSchema.isBlank()) {
                database.setDefaultSchemaName(liquibaseSchema);
                database.setLiquibaseSchemaName(liquibaseSchema);
            }

            try (var stmt = connection.createStatement()) {
                stmt.execute("SET search_path TO app_data, public");
            }

            Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            log.info("Liquibase migrations completed successfully.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to run Liquibase migrations", e);
        }
    }

}
