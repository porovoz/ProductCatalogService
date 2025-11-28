package com.bestapp.com.controller;

import com.bestapp.com.apiExceptionHandler.ApiResponse;
import com.bestapp.com.apiExceptionHandler.ApiResponseHandler;
import com.bestapp.com.dto.Login;
import com.bestapp.com.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authorization
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for authentication")
public class AuthController {

    private final AuthService authService;
    private final ApiResponseHandler apiResponseHandler;

    /**
     * User authorization process.
     */
    @Operation(
            summary = "User authorization",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Authorization"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login login, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            return apiResponseHandler.createErrorResponse("Already logged in as: " + username);
        }
        if (authService.login(login.getUsername(), login.getPassword())) {
            return apiResponseHandler.createSuccessResponse("Login successful.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * User logout process.
     */
    @Operation(
            summary = "User logout",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Logout"
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return apiResponseHandler.createErrorResponse("No user is currently logged in.");
        }
        authService.logout();
        session.invalidate();
        return apiResponseHandler.createSuccessResponse("Logged out successfully.");
    }
}
