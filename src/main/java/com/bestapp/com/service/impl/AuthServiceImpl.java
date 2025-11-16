package com.bestapp.com.service.impl;

import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import com.bestapp.com.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

/**
 * Simple in-memory user authentication.
 * Stores username->password pairs and tracks the current logged-in user.
 */
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final UserRepository userRepository = new UserRepository();
    private String currentUser = null;

    /**
     * Attempts to log in with given credentials.
     *
     * @param username username
     * @param password password
     * @return true if credentials are valid, false otherwise
     */
    @Override
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) return false;
        User user = maybeUser.get();
        boolean matches = passwordEncoder.matches(password, user.getPasswordHash());
        if (matches) {
            currentUser = user.getUsername();
        }
        return matches;
    }

    /**
     * Logs out the currently logged-in user.
     */
    @Override
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns currently logged-in user or null if none.
     *
     * @return logged-in user or null
     */
    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * True if currently logged-in user is not null.
     *
     * @return logged-in username or null
     */
    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
