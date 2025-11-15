package com.bestapp.com.service;

/**
 * Simple in-memory user authentication.
 * Stores username->password pairs and tracks the current logged-in user.
 */
public interface AuthService {

    /**
     * Attempts to log in with given credentials.
     */
    boolean login(String username, String password);

    /**
     * Logs out the currently logged-in user.
     */
    void logout();

    /**
     * Returns currently logged-in user or null if none.
     */
    String getCurrentUser();

    /**
     * True if currently logged-in user is not null.
     */
    boolean isLoggedIn();

}
