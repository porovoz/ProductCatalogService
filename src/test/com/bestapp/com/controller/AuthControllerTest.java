package com.bestapp.com.controller;

import com.bestapp.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("Register")
    void testRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\", \"role\": \"USER\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Login")
    void testLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\", \"role\": \"USER\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Get new access token")
    void testGetNewAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\", \"role\": \"USER\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        var loginResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String refreshToken = loginResponse.getResponse().getContentAsString();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/token")
                        .contentType("application/json")
                        .content("{ \"refreshToken\": \"" + refreshToken + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Get new refresh token")
    void testGetNewRefreshToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\", \"role\": \"USER\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        var loginResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType("application/json")
                        .content("{ \"username\": \"testuser\", \"password\": \"password123\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String refreshToken = loginResponse.getResponse().getContentAsString();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/refresh")
                        .contentType("application/json")
                        .content("{ \"refreshToken\": \"" + refreshToken + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }
}
