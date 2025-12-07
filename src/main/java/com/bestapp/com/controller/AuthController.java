package com.bestapp.com.controller;

import com.bestapp.com.dto.Register;
import com.bestapp.com.security.dto.JWTRequest;
import com.bestapp.com.security.dto.JWTResponse;
import com.bestapp.com.security.dto.RefreshJWTRequest;
import com.bestapp.com.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    /**
     * Register a new user.
     * @return the HTTP 201 status code (Created).<br>
     */
    @Operation(
            summary = "User registration",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Registration"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

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
    public ResponseEntity<JWTResponse> login(@RequestBody @Valid JWTRequest authRequest) {
        final JWTResponse tokenResponse = authService.login(authRequest);
        return ResponseEntity.ok(tokenResponse);
    }


    /**
     * Get a new access token when it became invalid.
     * @return the HTTP 200 status code (OK).<br>
     */
    @Operation(
            summary = "Gets a new access token when it became invalid",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Getting a new access token"
    )
    @PostMapping("/token")
    public ResponseEntity<JWTResponse> getNewAccessToken(@RequestBody RefreshJWTRequest request) {
        final JWTResponse tokenResponse = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Get a new access and refresh tokens when it became invalid.
     * @return the HTTP 200 status code (OK).<br>
     */
    @Operation(
            summary = "Gets a new access and refresh tokens when it became invalid",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Getting a new access and refresh tokens"
    )
    @PostMapping("/refresh")
    public ResponseEntity<JWTResponse> getNewRefreshToken(@RequestBody RefreshJWTRequest request) {
        final JWTResponse tokenResponse = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }
}
