package com.bestapp.ProductCatalog.controller;

import com.bestapp.com.apiExceptionHandler.ApiResponseHandler;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private ApiResponseHandler apiResponseHandler;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        apiResponseHandler = new ApiResponseHandler();
        authController = new AuthController(authService, apiResponseHandler);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("Login success")
    void loginSuccess() throws Exception {
        when(authService.login("user", "password")).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful."));
    }

    @Test
    @DisplayName("Login failure - invalid credentials")
    void loginFailureInvalidCredentials() throws Exception {
        when(authService.login("user", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"user\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Logout success")
    void logoutSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "user");

        doNothing().when(authService).logout();

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logged out successfully."));
    }

    @Test
    @DisplayName("Logout failure - no user in session")
    void logoutFailureNoUserInSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No user is currently logged in."));
    }

}
