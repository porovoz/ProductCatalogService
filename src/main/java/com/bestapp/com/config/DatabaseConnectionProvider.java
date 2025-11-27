package com.bestapp.com.config;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides method for creating a database connection.
 */
@RequiredArgsConstructor
public class DatabaseConnectionProvider {

    private final DatabaseConfigProperties configProperties;

    /**
     * Creates a new connection to the database using configuration properties.
     *
     * @return a new database connection
     * @throws SQLException if connection fails
     */
    public Connection createConnection() throws SQLException {
        String url = configProperties.getProperty("db.url");
        String username = configProperties.getProperty("db.username");
        String password = configProperties.getProperty("db.password");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Postgres JDBC driver not found on classpath", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

}
