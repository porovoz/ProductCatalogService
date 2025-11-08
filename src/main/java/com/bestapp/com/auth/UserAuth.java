package com.bestapp.com.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory user authentication.
 * Stores username->password pairs and tracks the current logged-in user.
 */
public class UserAuth {

    private final Map<String, String> users = new HashMap<>();
    private String loggedInUser = null;

    /**
     * Initializes the authentication with a couple of demo users.
     */
    public UserAuth() {
        users.put("admin", "admin");
        users.put("user1", "password1");
    }

    /**
     * Attempts to log in with given credentials.
     *
     * @param username username
     * @param password password
     * @return true if credentials are valid and user is logged in, false otherwise
     */
    public boolean login(String username, String password) {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            loggedInUser = username;
            System.out.println("Successful login");
            return true;
        }
        System.out.println("Invalid credentials");
        return false;
    }

    /**
     * Logs out the currently logged-in user.
     */
    public void logout() {
        loggedInUser = null;
        System.out.println("Logged out successfully");
    }

    /**
     * Returns currently logged-in username or null if none.
     *
     * @return logged-in username or null
     */
    public String getLoggedInUser() {
        return loggedInUser;
    }

}
