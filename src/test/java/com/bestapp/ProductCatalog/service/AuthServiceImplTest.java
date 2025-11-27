package com.bestapp.ProductCatalog.service;

import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import com.bestapp.com.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash(new BCryptPasswordEncoder(12).encode("password"));
        user.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        boolean result = authService.login("admin", "password");
        assertTrue(result);
        assertTrue(authService.isLoggedIn());
        assertEquals("admin", authService.getCurrentUser());
    }

    @Test
    void testLoginFailure() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        boolean result = authService.login("admin", "wrong");
        assertFalse(result);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }

    @Test
    void testLoginNonExistingUser() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        boolean loggedIn = authService.login("nonexistent", "password");
        assertFalse(loggedIn);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }

    @Test
    void testLogoutAndIsLoggedIn() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash(new BCryptPasswordEncoder(12).encode("password"));
        user.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        authService.login("admin", "password");

        assertTrue(authService.isLoggedIn());

        authService.logout();
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }

}
