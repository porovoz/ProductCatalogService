package com.bestapp.com.servlet;

import com.bestapp.com.cache.ProductCache;
import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.ProductService;
import com.bestapp.com.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * {@code CacheStatsServlet} - An HTTP servlet for retrieving product cache statistics.
 * <p>
 * This servlet handles GET requests to the endpoint {@code /api/cache/stats}, providing cache statistics
 * such as the number of cache hits and cache misses.
 * The cache statistics are accessible only to authenticated users.
 * </p>
 */
@WebServlet("/api/cache/stats")
public class CacheStatsServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductRepository(new DatabaseConfig()));
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String APPLICATION_JSON = "application/json";
    private static final String NEED_TO_LOGIN = "You need to log in first.";

    /**
     * Handles GET requests to retrieve cache statistics.
     * <p>
     * If the request is successful, a JSON response is returned with the number of cache hits and misses:
     * {@code {"cacheHits": <count>, "cacheMisses": <count>}}.
     * In case of an error, an error message with the appropriate HTTP status is returned.
     * </p>
     *
     * @param request  The HTTP request from the client.
     * @param response The HTTP response to send back to the client.
     * @throws ServletException if an error occurs during request processing.
     * @throws IOException if an error occurs during response writing.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = getLoggedUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + NEED_TO_LOGIN + "\"}");
            return;
        }
        try {
            ProductCache cache = productService.getCache();
            if (cache == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType(APPLICATION_JSON);
                response.getWriter().write("{\"error\": \"Failed to get cache stats.\"}");
                return;
            }

            int cacheHits = cache.getCacheHits();
            int cacheMisses = cache.getCacheMisses();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(APPLICATION_JSON);
            String jsonResponse = objectMapper.writeValueAsString(Map.of("cacheHits", cacheHits, "cacheMisses", cacheMisses));
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            log("Error getting cache stats: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"An unexpected error occurred while getting cache stats.\"}");
        }
    }

    /**
     * Authorization check through HttpSession.
     */
    private String getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("username");
    }

}
