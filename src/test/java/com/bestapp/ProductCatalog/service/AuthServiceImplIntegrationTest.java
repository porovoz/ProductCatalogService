package com.bestapp.ProductCatalog.service;

import com.bestapp.com.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("productCatalogService")
                    .withUsername("alex")
                    .withPassword("alexSecret");

    private AuthServiceImpl authService;

    @BeforeAll
    void setup() {
        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());

        authService = new AuthServiceImpl();
    }

    @Test
    void testLoginWithAdminUser() {
        boolean loginResult = authService.login("admin", "password");

        assertTrue(loginResult);
        assertEquals("admin", authService.getCurrentUser());
        assertTrue(authService.isLoggedIn());
    }

    @Test
    void testLoginWithWrongPassword() {
        boolean loginResult = authService.login("admin", "wrongpassword");

        assertFalse(loginResult);
    }

    @Test
    void testLoginWithNonExistingUser() {
        boolean loginResult = authService.login("nonexistent", "password");

        assertFalse(loginResult);
    }

    @Test
    void testLogout() {
        authService.login("admin", "password");

        assertTrue(authService.isLoggedIn());

        authService.logout();

        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }
}
