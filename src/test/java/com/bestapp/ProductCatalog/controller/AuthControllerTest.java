package com.bestapp.ProductCatalog.controller;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.view.ConsoleView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private ConsoleView consoleView;
    private AuditLogger auditLogger;
    private AuthController authController;

    @BeforeEach
    void setup() {
        authService = mock(AuthService.class);
        consoleView = mock(ConsoleView.class);
        auditLogger = mock(AuditLogger.class);
        authController = new AuthController(authService, consoleView, auditLogger);
    }

    @Test
    void loginSuccess() {
        when(consoleView.read(any())).thenReturn("user1", "password1");
        when(authService.login("user1", "password1")).thenReturn(true);

        authController.login();

        verify(auditLogger).log("user1 logged in");
        verify(consoleView).showMessage("Login successful.");
    }

    @Test
    void loginFails() {
        when(consoleView.read(any())).thenReturn("user2", "password2");
        when(authService.login("user2", "password2")).thenReturn(false);

        authController.login();

        verify(consoleView).showMessage("Invalid credentials.");
    }

    @Test
    void logoutWorks() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn("admin");

        authController.logout();

        verify(authService).logout();
        verify(auditLogger).log("admin logged out");
    }

    @Test
    void logoutWhenNotLoggedIn() {
        when(authService.isLoggedIn()).thenReturn(false);

        authController.logout();

        verify(consoleView).showMessage("No user is currently logged in.");
    }

    @Test
    void requireLoginReturnsFalseIfNotLogged() {
        when(authService.isLoggedIn()).thenReturn(false);

        boolean result = authController.requireLogin();

        verify(consoleView).showMessage("You need to log in first.");
        assertFalse(result);
    }

    @Test
    void requireLoginReturnsTrueIfLogged() {
        when(authService.isLoggedIn()).thenReturn(true);

        assertTrue(authController.requireLogin());
    }

}
