package com.bestapp.com.controller;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.view.ConsoleView;
import lombok.RequiredArgsConstructor;

/**
 * Controller responsible for user authentication flow:
 * login, logout and user session checks.
 */
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ConsoleView consoleView;
    private final AuditLogger auditLogger;

    /**
     * Handles the login process:
     * <ul>
     *     <li>prompts for username</li>
     *     <li>prompts for password</li>
     *     <li>delegates verification to {@link AuthService#login}</li>
     * </ul>
     *
     * <p>On success, writes an audit log entry.</p>
     */
    public void login() {
        if (authService.getCurrentUser() != null) {
            consoleView.showMessage("Already logged in as: " + authService.getCurrentUser());
            return;
        }
        String username = consoleView.read("Enter username: ");
        String password = consoleView.read("Enter password: ");
        if (authService.login(username, password)) {
            auditLogger.log(username + " logged in");
            consoleView.showMessage("Login successful.");
        } else {
            consoleView.showMessage("Invalid credentials.");
        }
    }

    /**
     * Logs out the current user if logged in.
     * Writes audit log entry on success.
     */
    public void logout() {
        if (!authService.isLoggedIn()) {
            consoleView.showMessage("No user is currently logged in.");
            return;
        }
        auditLogger.log(authService.getCurrentUser() + " logged out");
        authService.logout();
        consoleView.showMessage("Logged out successfully.");
    }

    /**
     * Utility method that checks whether a user is logged in.
     *
     * @return {@code true} if no user is logged in (i.e., access should be denied)
     */
    public boolean requireLogin() {
        if (!authService.isLoggedIn()) {
            consoleView.showMessage("You need to log in first.");
            return false;
        }
        return true;
    }

}
