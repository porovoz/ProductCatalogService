package com.bestapp.com.service;

import com.bestapp.com.dto.Register;
import com.bestapp.com.dto.UserDTO;
import com.bestapp.com.exception.notFoundException.UserNotFoundException;
import com.bestapp.com.model.RoleType;
import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@Testcontainers
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Container
    public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("productCatalogService")
            .withUsername("alex")
            .withPassword("alexSecret");

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create User - Success")
    void testCreateUser() {
        Register registerRequest = new Register("testuser", "password123", RoleType.USER);

        userService.createUser(registerRequest);

        Optional<UserDTO> userDTOOptional = userService.getByUsername("testuser");

        assertThat(userDTOOptional).isPresent();
        UserDTO userDTO = userDTOOptional.get();
        assertThat(userDTO.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Create User - User already exists")
    void testCreateUser_UserExists() {
        Register registerRequest = new Register("testuser", "password123", RoleType.USER);

        userService.createUser(registerRequest);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.createUser(registerRequest);
        });

        assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("Get User by Username - Success")
    void testGetUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("password123");
        user.setRole(RoleType.USER);

        userRepository.save(user);

        Optional<UserDTO> result = userService.getByUsername("testuser");

        assertThat(result).isPresent();
        UserDTO userDTO = result.get();
        assertThat(userDTO.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Get User by Username - User Not Found")
    void testGetUserByUsername_NotFound() {
        Optional<UserDTO> userDTOOptional = userService.getByUsername("nonexistentuser");

        assertThat(userDTOOptional).isEmpty();
    }
}
