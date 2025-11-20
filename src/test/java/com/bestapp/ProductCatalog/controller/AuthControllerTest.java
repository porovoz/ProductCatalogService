package com.bestapp.ProductCatalog.controller;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.view.ConsoleView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private ConsoleView consoleView;
    private AuditLogger auditLogger;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        consoleView = mock(ConsoleView.class);
        auditLogger = mock(AuditLogger.class);
        authController = new AuthController(authService, consoleView, auditLogger);
    }

    @Test
    void testLoginSuccess() {

        AtomicBoolean firstCall = new AtomicBoolean(true);

        when(authService.getCurrentUser()).thenAnswer(invocation -> {
            if (firstCall.getAndSet(false)) {
                return null;
            }
            return "user1";
        });

        when(consoleView.read("Enter username: ")).thenReturn("user1");
        when(consoleView.read("Enter password: ")).thenReturn("pass1");

        when(authService.login("user1", "pass1")).thenReturn(true);

        authController.login();

        verify(consoleView).showMessage("Login successful.");
        verify(auditLogger).log("user1 logged in");
    }

    @Test
    void testLoginAlreadyLoggedIn() {
        when(authService.getCurrentUser()).thenReturn("user1");

        authController.login();

        verify(consoleView).showMessage("Already logged in as: user1");
        verifyNoInteractions(auditLogger);
    }

    @Test
    void testLoginInvalidCredentials() {
        when(authService.getCurrentUser()).thenReturn(null);
        when(consoleView.read("Enter username: ")).thenReturn("user1");
        when(consoleView.read("Enter password: ")).thenReturn("wrongpass");
        when(authService.login("user1", "wrongpass")).thenReturn(false);

        authController.login();

        verify(consoleView).showMessage("Invalid credentials.");
        verifyNoInteractions(auditLogger);
    }

    @Test
    void testLogoutSuccess() {
        when(authService.isLoggedIn()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn("user1");

        authController.logout();

        verify(auditLogger).log("user1 logged out");
        verify(authService).logout();
        verify(consoleView).showMessage("Logged out successfully.");
    }

    @Test
    void testLogoutNoUser() {
        when(authService.isLoggedIn()).thenReturn(false);

        authController.logout();

        verify(consoleView).showMessage("No user is currently logged in.");
        verifyNoInteractions(auditLogger);
    }

    @Test
    void testRequireLogin() {
        when(authService.isLoggedIn()).thenReturn(false);
        boolean result = authController.requireLogin();
        assertFalse(result);
        verify(consoleView).showMessage("You need to log in first.");

        when(authService.isLoggedIn()).thenReturn(true);
        result = authController.requireLogin();
        assertTrue(result);
    }

}
