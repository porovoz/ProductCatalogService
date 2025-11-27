package com.bestapp.com.servlet;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.dto.Login;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code LoginServlet} - A servlet that handles user login requests.
 * <p>
 * This servlet listens for POST requests at the {@code /api/auth/login} URL and performs the following actions:
 * <ul>
 *   <li>Checks if the user is already logged in by examining the HTTP session.</li>
 *   <li>Validates the login credentials provided in the request body.</li>
 *   <li>Attempts to authenticate the user using the {@link AuthService}.</li>
 *   <li>If the login is successful, it creates a new session and stores the username in the session.</li>
 *   <li>Logs the login event using the {@link AuditLogger}.</li>
 *   <li>Returns appropriate HTTP status codes and error messages if any issue occurs during the process.</li>
 * </ul>
 * </p>
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthServiceImpl();
    private final AuditLogger auditLogger = new AuditLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String APPLICATION_JSON = "application/json";

    /**
     * Handles POST requests for user login.
     * <p>
     * The method processes the login credentials, validates them, and attempts to authenticate the user.
     * If successful, a new session is created, and the username is stored in the session. If the login fails,
     * the method returns a {@code 401 Unauthorized} status with a corresponding error message.
     * </p>
     *
     * @param request  The HTTP request containing the login credentials.
     * @param response The HTTP response that will be sent back to the client.
     * @throws IOException If an I/O error occurs while reading the request or writing the response.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.getWriter().write("Already logged in as: " + session.getAttribute("username"));
            return;
        }

        try {
            Login login = objectMapper.readValue(request.getInputStream(), Login.class);

            Set<ConstraintViolation<Login>> violations = validator.validate(login);
            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(APPLICATION_JSON);
                String errors = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                response.getWriter().write("{\"error\": \"" + errors + "\"}");
                return;
            }

            boolean success = authService.login(login.getUsername(), login.getPassword());
            if (success) {

                // Create new session if absent
                session = request.getSession(true);
                session.setAttribute("username", login.getUsername());

                auditLogger.log(login.getUsername() + " logged in");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Login successful.");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid credentials.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid JSON format.\"}");
        }
    }

}
