package com.bestapp.com.service;

import com.bestapp.com.dto.Register;
import com.bestapp.com.security.dto.JWTRequest;
import com.bestapp.com.security.dto.JWTResponse;
import lombok.NonNull;

/**
 * Simple in-memory user authentication.
 * Stores username->password pairs and tracks the current logged-in user.
 */
public interface AuthService {

    /**
     * Checks the correctness of the entered credentials when trying to log in.
     * @param authenticationRequest an object containing information to proceed successful login.
     */
    JWTResponse login(@NonNull JWTRequest authenticationRequest);

    /**
     * Gets a new access token when it became invalid.
     * @param refreshToken an object containing refresh token to get a new access token.
     */
    JWTResponse getAccessToken(@NonNull String refreshToken);

    /**
     * Registers a new user in the application.
     * @param register an object containing information about user registration.
     * @return <B>true</B> if the registration was successful, otherwise <B>false</B> .
     */
    boolean register(Register register);

    /**
     * Gets a new access and refresh tokens when it became invalid.
     * @param refreshToken an object containing refresh token to get a new access and refresh tokens.
     */
    JWTResponse refreshToken(@NonNull String refreshToken);

}
