package com.bestapp.com.service;

import com.bestapp.com.security.dto.JWTRequest;
import com.bestapp.com.security.dto.JWTResponse;
import com.bestapp.com.dto.Register;
import com.bestapp.com.exception.InvalidTokenException;
import com.bestapp.com.model.RoleType;
import com.bestapp.com.repository.UserRepository;
import com.bestapp.com.service.impl.AuthServiceImpl;
import com.bestapp.com.security.JWTProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class AuthServiceImplTest {

    @Container
    public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("productCatalogService")
            .withUsername("alex")
            .withPassword("alexSecret");

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTProvider jwtProvider;

    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Register")
    void testRegister() {
        Register register = new Register("newuser", "password123", RoleType.USER);

        boolean result = authService.register(register);

        assertThat(result).isTrue();

        assertThat(userRepository.existsByUsername("newuser")).isTrue();
    }

    @Test
    @DisplayName("Login")
    void testLogin() {
        Register register = new Register("testuser", "password123", RoleType.USER);
        authService.register(register);

        JWTRequest loginRequest = new JWTRequest("testuser", "password123");

        JWTResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();

        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
    }

    @Test
    @DisplayName("Get new access token")
    void testGetNewAccessToken() {
        Register register = new Register("testuser", "password123", RoleType.USER);
        authService.register(register);
        JWTRequest loginRequest = new JWTRequest("testuser", "password123");
        JWTResponse loginResponse = authService.login(loginRequest);
        String refreshToken = loginResponse.getRefreshToken();

        JWTResponse response = authService.getAccessToken(refreshToken);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isEmpty();
    }

    @Test
    @DisplayName("Get refresh token")
    void testRefreshToken() {
        Register register = new Register("testuser", "password123", RoleType.USER);
        authService.register(register);
        JWTRequest loginRequest = new JWTRequest("testuser", "password123");
        JWTResponse loginResponse = authService.login(loginRequest);
        String refreshToken = loginResponse.getRefreshToken();

        JWTResponse response = authService.refreshToken(refreshToken);

        assertThat(response).isNotNull();
        assertThat(response.getRefreshToken()).isNotEmpty();
    }

    @Test
    @DisplayName("Invalid refresh token")
    void testInvalidRefreshToken() {
        String invalidToken = "invalid_refresh_token";

        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidTokenException.class,
                () -> authService.getAccessToken(invalidToken)
        );
    }

}
