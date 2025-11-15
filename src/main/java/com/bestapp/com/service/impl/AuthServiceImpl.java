package com.bestapp.com.service.impl;

import com.bestapp.com.service.AuthService;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory user authentication.
 * Stores username->password pairs and tracks the current logged-in user.
 */
public class AuthServiceImpl implements AuthService {

    private final Map<String, String> users = new HashMap<>();
    private String currentUser = null;

    /**
     * Initializes the authentication with a couple of demo users.
     */
    public AuthServiceImpl() {
        users.put("admin", "admin");
        users.put("user1", "password1");
    }

    /**
     * Attempts to log in with given credentials.
     *
     * @param username username
     * @param password password
     * @return true if credentials are valid, false otherwise
     */
    @Override
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String expected = users.get(username);
        if (expected != null && expected.equals(password)) {
            currentUser = username;
            return true;
        }
        return false;
    }

    /**
     * Logs out the currently logged-in user.
     */
    @Override
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns currently logged-in user or null if none.
     *
     * @return logged-in user or null
     */
    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * True if currently logged-in user is not null.
     *
     * @return logged-in username or null
     */
    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
