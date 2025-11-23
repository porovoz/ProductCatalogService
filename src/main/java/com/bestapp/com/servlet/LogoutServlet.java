package com.bestapp.com.servlet;

import com.bestapp.com.audit.AuditLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/auth/logout")
public class LogoutServlet extends HttpServlet {

    private final AuditLogger auditLogger = new AuditLogger();

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
