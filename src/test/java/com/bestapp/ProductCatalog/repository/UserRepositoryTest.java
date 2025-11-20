package com.bestapp.ProductCatalog.repository;

import com.bestapp.ProductCatalog.BasePostgresTest;
import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BasePostgresTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(new DatabaseConfig());
    }

//    @Test
//    void testSaveAndFindByUsername() {
//        User user = new User();
//        user.setUsername("john");
//        user.setPasswordHash("hashedpassword");
//        user.setRole("USER");
//
//        userRepository.save(user);
//        assertNotNull(user.getId(), "User ID should be generated");
//
//        Optional<User> retrieved = userRepository.findByUsername("john");
//        assertTrue(retrieved.isPresent());
//        assertEquals("john", retrieved.get().getUsername());
//        assertEquals("hashedpassword", retrieved.get().getPasswordHash());
//        assertEquals("USER", retrieved.get().getRole());
//    }

    @Test
    void testFindNonExistingUser() {
        Optional<User> user = userRepository.findByUsername("nonexist");
        assertTrue(user.isEmpty());
    }

}
