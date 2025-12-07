package com.bestapp.com.service.impl;

import com.bestapp.com.dto.Register;
import com.bestapp.com.dto.UserDTO;
import com.bestapp.com.exception.InvalidTokenException;
import com.bestapp.com.exception.notFoundException.UserNotFoundException;
import com.bestapp.com.exception.invalidRegistrationParameterException.InvalidLoginPasswordException;
import com.bestapp.com.model.RoleType;
import com.bestapp.com.repository.UserRepository;
import com.bestapp.com.security.JWTProvider;
import com.bestapp.com.security.dto.JWTRequest;
import com.bestapp.com.security.dto.JWTResponse;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bestapp.com.model.RoleType.USER;

/**
 * User authentication with JWT.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final UserService userService;
    private final JWTProvider jwtProvider;
    private final Map<String, String> refreshStorage = new HashMap<>();

    /**
     * Registers a new user in the application and saves it to a database.<br>
     * - {@link UserService#createUser(Register)} method used to create a new user.<br>
     * - Converting register data transfer object into user object {@link com.bestapp.com.service.UserMapper#registerToUser(Register)}.
     * @param register an object containing information about user registration.
     * @return <B>true</B> if the registration was successful, otherwise <B>false</B>.
     */
    @Override
    public boolean register(Register register) {
        if (userRepository.existsByUsername(register.getUsername())) {
            log.info("User already exists.");
            return false;
        }
        RoleType role = (register.getRole() == null) ? USER : register.getRole();
        register.setRole(role);
        userService.createUser(register);
        log.info("User was successfully registered.");
        return true;
    }

    /**
     * Checks the correctness of the entered credentials when trying to log in.
     * - {@link UserService#getByUsername(String)} method used to get user by username from DB.<br>
     * - {@link JWTProvider#generateAccessToken(UserDTO)} method used to generate access token.<br>
     * - {@link JWTProvider#generateRefreshToken(UserDTO)} method used to generate refresh token.<br>
     * - {@link Map#put(Object, Object)} method used to save refresh token.<br>
     * @exception UserNotFoundException may throw if the user not found in DB.<br>
     * @exception InvalidLoginPasswordException may throw if entered password is incorrect.<br>
     * @param authenticationRequest an object containing information to proceed successful login.
     * @return {@link JWTResponse} - JWT response data transfer object with access and refresh tokens.
     */
    @Override
    public JWTResponse login(@NonNull JWTRequest authenticationRequest) {
        final UserDTO user = userService.getByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(super.toString()));
        if (passwordEncoder.matches(authenticationRequest.getPassword(), user.getPasswordHash())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getUsername(), refreshToken);
            log.info("User was successfully logged in");
            return new JWTResponse(accessToken, refreshToken);
        } else {
            throw new InvalidLoginPasswordException("Incorrect password");
        }
    }

    /**
     * Gets a new access token when it became invalid.
     * - {@link JWTProvider#validateRefreshToken(String)} method used to validate entered refresh token.<br>
     * - {@link JWTProvider#getRefreshClaims(String)} method used to get refresh claims.<br>
     * - {@link Map#get(Object)} method used to get refresh token.<br>
     * - {@link UserService#getByUsername(String)} method used to get user by username from DB.<br>
     * - {@link JWTProvider#generateAccessToken(UserDTO)} method used to generate access token.<br>
     * @exception UserNotFoundException may throw if the user not found in DB.<br>
     * @exception InvalidTokenException may throw if entered refresh token is invalid.<br>
     * @param refreshToken an object containing refresh token to get a new access token.<br>
     * @return {@link JWTResponse} - JWT response data transfer object with a new access token.
     */
    @Override
    public JWTResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDTO user = userService.getByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(super.toString()));
                final String accessToken = jwtProvider.generateAccessToken(user);
                log.info("New access token was successfully got");
                return new JWTResponse(accessToken, "");
            }
        }
        throw new InvalidTokenException("Invalid refresh token");
    }

    /**
     * Gets a new access and refresh tokens when it became invalid.
     * - {@link JWTProvider#validateRefreshToken(String)} method used to validate entered refresh token.<br>
     * - {@link JWTProvider#getRefreshClaims(String)} method used to get refresh claims.<br>
     * - {@link Map#get(Object)} method used to get refresh token.<br>
     * - {@link UserService#getByUsername(String)} method used to get user by username from DB.<br>
     * - {@link JWTProvider#generateAccessToken(UserDTO)} method used to generate access token.<br>
     * - {@link JWTProvider#generateRefreshToken(UserDTO)} method used to generate refresh token.<br>
     * - {@link Map#put(Object, Object)} method used to save refresh token.<br>
     * @exception UserNotFoundException may throw if the user not found in DB.<br>
     * @exception InvalidTokenException may throw if entered refresh token is invalid.<br>
     * @param refreshToken an object containing refresh token to get a new access and refresh tokens.
     * @return {@link JWTResponse} - JWT response data transfer object with a new access and refresh tokens.
     */
    @Override
    public JWTResponse refreshToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDTO user = userService.getByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(super.toString()));
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getUsername(), newRefreshToken);
                log.info("New access and refresh tokens was successfully got");
                return new JWTResponse(accessToken, newRefreshToken);
            }
            log.info("Tokens are not equal.");
        }
        throw new InvalidTokenException("Invalid refresh token");
    }
}
