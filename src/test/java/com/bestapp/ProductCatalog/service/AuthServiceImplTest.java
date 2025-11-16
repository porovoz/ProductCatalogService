package com.bestapp.ProductCatalog.service;

import com.bestapp.ProductCatalog.BasePostgresTest;
import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import com.bestapp.com.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest extends BasePostgresTest {

    private AuthServiceImpl authService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl();
        userRepository = new UserRepository();
    }

//    @Test
//    void testLoginLogout() {
//        String rawPassword = "secret";
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        User user = new User();
//        user.setUsername("alice");
//        user.setPasswordHash(encoder.encode(rawPassword));
//        user.setRole("USER");
//        userRepository.save(user);
//
//        assertFalse(authService.isLoggedIn());
//        assertTrue(authService.login("alice", rawPassword));
//        assertTrue(authService.isLoggedIn());
//        assertEquals("alice", authService.getCurrentUser());
//
//        authService.logout();
//        assertFalse(authService.isLoggedIn());
//    }
//
//    @Test
//    void testLoginWithInvalidCredentials() {
//        assertFalse(authService.login("nonexist", "pass"));
//    }

}
