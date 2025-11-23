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

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthServiceImpl();
    private final AuditLogger auditLogger = new AuditLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String APPLICATION_JSON = "application/json";

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
