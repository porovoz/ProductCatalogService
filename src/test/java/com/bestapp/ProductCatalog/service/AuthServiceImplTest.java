package com.bestapp.ProductCatalog.service;

import com.bestapp.com.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl();
    }

    @Test
    void loginSuccess() {
        assertTrue(authService.login("admin", "admin"));
        assertEquals("admin", authService.getCurrentUser());
    }

    @Test
    void loginFailWrongPassword() {
        assertFalse(authService.login("admin", "wrong"));
        assertNull(authService.getCurrentUser());
    }

    @Test
    void loginFailUnknownUser() {
        assertFalse(authService.login("unknown", "123"));
    }

    @Test
    void loginFailNullValues() {
        assertFalse(authService.login(null, null));
        assertNull(authService.getCurrentUser());
    }

    @Test
    void logoutClearsCurrentUser() {
        authService.login("admin", "admin");
        authService.logout();
        assertNull(authService.getCurrentUser());
    }

    @Test
    void isLoggedInWorks() {
        assertFalse(authService.isLoggedIn());
        authService.login("admin", "admin");
        assertTrue(authService.isLoggedIn());
    }

}
