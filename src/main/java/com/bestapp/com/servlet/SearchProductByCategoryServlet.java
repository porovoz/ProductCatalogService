package com.bestapp.com.servlet;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.ProductMapper;
import com.bestapp.com.service.ProductService;
import com.bestapp.com.service.impl.AuthServiceImpl;
import com.bestapp.com.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/products/category")
public class SearchProductByCategoryServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductRepository(new DatabaseConfig()));
    private final AuthService authService = new AuthServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String APPLICATION_JSON = "application/json";
    private static final String NEED_TO_LOGIN = "You need to log in first.";
    private static final String CATEGORY_PARAM = "category";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = getLoggedUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + NEED_TO_LOGIN + "\"}");
            return;
        }

        String category = request.getParameter(CATEGORY_PARAM);
        if (category == null || category.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"Category parameter is required.\"}");
            return;
        }
        try {
            List<Product> products = productService.getByCategory(category);

            if (products.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType(APPLICATION_JSON);
                response.getWriter().write("{\"error\": \"No products found in this category.\"}");
                return;
            }

            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductMapper.INSTANCE::productToProductDTO)
                    .toList();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write(objectMapper.writeValueAsString(productDTOs));
        } catch (Exception e) {
            log("Error getting products by category: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"An unexpected error occurred while getting products by category.\"}");
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
