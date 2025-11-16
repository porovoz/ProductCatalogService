package com.bestapp.com.repository;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Represents user repository.
 */
public class UserRepository {

    private final Connection connection;
    public UserRepository() {
        this.connection = DatabaseConfig.getConnection();
    }

    /**
     * Searches user by username.
     *
     * @param username category name
     * @return user of matching name
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role FROM app_data.users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a new user to the repository.
     *
     * @param user new user to add
     */
    public void save(User user) {
        String sql = "INSERT INTO app_data.users (username, password_hash, role) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) user.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
