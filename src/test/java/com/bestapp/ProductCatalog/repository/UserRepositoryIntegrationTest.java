package com.bestapp.ProductCatalog.repository;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("productCatalogService")
                    .withUsername("alex")
                    .withPassword("alexSecret");

    private UserRepository userRepository;

    @BeforeAll
    void setup() {
        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());

        DatabaseConfig databaseConfig = new DatabaseConfig();
        userRepository = new UserRepository(databaseConfig);
    }

    @Test
    void testSaveAndFindByUsername() {
        User user = new User();
        user.setUsername("testUser");
        user.setPasswordHash("$2a$12$hashhashhashhashhashhashhashhash"); // bcrypt hash
        user.setRole("USER");

        userRepository.save(user);
        assertNotNull(user.getId());

        Optional<User> found = userRepository.findByUsername("testUser");
        assertTrue(found.isPresent());
        assertEquals("testUser", found.get().getUsername());
    }

    @Test
    void testFindNonExistingUser() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertTrue(found.isEmpty());
    }
}
