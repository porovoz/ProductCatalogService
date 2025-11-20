package com.bestapp.com.config;

import java.sql.Connection;

/**
 * DatabaseConfig - initializes classes that: read properties, create a database connection, run migrations.
 */
public class DatabaseConfig {

    private final DatabaseConfigProperties configProperties;
    private final DatabaseConnectionProvider connectionProvider;
    private final LiquibaseMigrationRunner migrationRunner;

    public DatabaseConfig() {
        this.configProperties = new DatabaseConfigProperties();
        this.connectionProvider = new DatabaseConnectionProvider(configProperties);
        this.migrationRunner = new LiquibaseMigrationRunner(configProperties);
    }

    /**
     * Creates a connection with migrations.
     */
    public Connection createConnectionWithMigrations() {
        try {
            Connection connection = connectionProvider.createConnection();
            migrationRunner.runMigrations(connection);
            return connection;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create database connection", e);
        }
    }

}