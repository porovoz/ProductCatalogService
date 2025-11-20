package com.bestapp.com.repository;

/**
 * Enum representing SQL queries for interacting with the `app_data.users` table in the database.
 * Each constant in this enum corresponds to a specific SQL query used for CRUD (Create, Read, Update, Delete)
 * operations on the `users` table. This enum includes queries for selecting users by username and inserting new users.
 * The `getQuery()` method allows retrieval of the SQL query string associated with each constant.
 */
public enum UserRepositorySql {

    SELECT_BY_USERNAME("SELECT id, username, password_hash, role FROM app_data.users WHERE username = ?"),

    INSERT_USER("INSERT INTO app_data.users (username, password_hash, role) VALUES (?, ?, ?) RETURNING id");

    private final String query;

    UserRepositorySql(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

}
