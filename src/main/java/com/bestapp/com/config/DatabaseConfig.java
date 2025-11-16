package com.bestapp.com.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DatabaseConfig - singleton connection provider.
 * Reads properties from application.properties but allows
 * overriding via system properties: db.url, db.username, db.password.
 * Also runs Liquibase migrations on first connection.
 */
@Slf4j
public class DatabaseConfig {

    private static Connection connection;
    private static boolean migrationsRun = false;

    public static synchronized Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            Properties properties = new Properties();
            try (InputStream in = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (in != null) properties.load(in);
            }

            String url = System.getProperty("db.url", properties.getProperty("db.url"));
            String username = System.getProperty("db.username", properties.getProperty("db.username"));
            String password = System.getProperty("db.password", properties.getProperty("db.password"));
            String liquibaseChangelog = System.getProperty("liquibase.changelog", properties.getProperty("liquibase.changelog"));
            String liquibaseSchema = System.getProperty("liquibase.schema", properties.getProperty("liquibase.schema"));

            connection = DriverManager.getConnection(url, username, password);

            if (!migrationsRun && liquibaseChangelog != null && !liquibaseChangelog.isBlank()) {
                runMigrations(connection, liquibaseChangelog, liquibaseSchema);
                migrationsRun = true;
            }

            return connection;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create DB connection: " + e.getMessage(), e);
        }
    }

    private static void runMigrations(Connection conn, String changelogPath, String liquibaseSchema) throws Exception {
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(conn));
        if (liquibaseSchema != null && !liquibaseSchema.isBlank()) {
            database.setDefaultSchemaName(liquibaseSchema);
            database.setLiquibaseSchemaName(liquibaseSchema);
        }

        // Установка search_path для корректной работы nextval('app_data.product_seq')
        try (var stmt = conn.createStatement()) {
            stmt.execute("SET search_path TO app_data, public");
        }

        Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database);
        liquibase.update();
        log.info("Migration is completed successfully.");
    }

}