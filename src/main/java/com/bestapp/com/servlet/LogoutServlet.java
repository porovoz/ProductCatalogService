package com.bestapp.com.servlet;

import com.bestapp.com.audit.AuditLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * {@code LogoutServlet} - A servlet that handles user logout requests.
 * <p>
 * This servlet listens for POST requests at the {@code /api/auth/logout} URL and performs the following actions:
 * <ul>
 *   <li>Checks if the user is currently logged in by examining the HTTP session.</li>
 *   <li>If the user is logged in, it invalidates the session and logs the logout event using {@link AuditLogger}.</li>
 *   <li>If no user is logged in (i.e., no valid session), it returns a {@code 400 Bad Request} response.</li>
 *   <li>Returns appropriate HTTP status codes and messages depending on the logout outcome.</li>
 * </ul>
 * </p>
 */
@WebServlet("/api/auth/logout")
public class LogoutServlet extends HttpServlet {

    private final AuditLogger auditLogger = new AuditLogger();

    /**
     * Handles POST requests for user logout.
     * <p>
     * This method checks if the user is logged in by inspecting the HTTP session. If a valid session exists, the user's session is invalidated,
     * and a logout event is logged. If no session exists or the user is not logged in, it returns a {@code 400 Bad Request} status with an error message.
     * </p>
     *
     * @param request  The HTTP request containing the session information.
     * @param response The HTTP response that will be sent back to the client, indicating whether the logout was successful.
     * @throws ServletException If the request cannot be processed properly by the servlet.
     * @throws IOException If an I/O error occurs while reading or writing the response.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("username") == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("No user is currently logged in.");
                return;
            }

            String currentUser = (String) session.getAttribute("username");
            auditLogger.log(currentUser + " logged out");
            session.invalidate();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Logged out successfully.");
    }

}
