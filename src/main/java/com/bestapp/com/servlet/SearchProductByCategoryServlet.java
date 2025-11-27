package com.bestapp.com.servlet;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.ProductMapper;
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
import java.util.List;

/**
 * {@code SearchProductByCategoryServlet} is a servlet that handles HTTP GET requests to search for products by category.
 * <p>
 * This servlet listens for requests at the URL pattern {@code /api/products/category}. It expects a query parameter {@code category}
 * in the request, which is used to filter products by their category.
 * </p>
 * <p>
 * The servlet performs the following tasks:
 * <ul>
 *   <li>Checks if the user is authenticated by inspecting the session. If the user is not logged in, a {@code 401 Unauthorized}
 *       response is returned.</li>
 *   <li>Validates the {@code category} query parameter. If the parameter is missing or empty, a {@code 400 Bad Request}
 *       response is returned.</li>
 *   <li>Gets products that match the provided category using {@link ProductService#getByCategory(String)}.</li>
 *   <li>If products are found, they are returned as a JSON array of {@link ProductDTO} objects with a {@code 200 OK} status.</li>
 *   <li>If no products are found for the given category, a {@code 404 Not Found} response is returned.</li>
 *   <li>In case of an internal error (e.g., database issues), a {@code 500 Internal Server Error} response is returned.</li>
 * </ul>
 * </p>
 */
@WebServlet("/api/products/category")
public class SearchProductByCategoryServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductRepository(new DatabaseConfig()));
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String APPLICATION_JSON = "application/json";
    private static final String NEED_TO_LOGIN = "You need to log in first.";
    private static final String CATEGORY_PARAM = "category";


    /**
     * Handles the {@code GET} request to search for products by category.
     * <p>
     * This method retrieves the {@code category} query parameter from the request. If the user is authenticated and the category
     * parameter is valid, it gets the products that belong to the specified category using the {@link ProductService}.
     * The products are then returned as a list of {@link ProductDTO} objects in JSON format. If any errors occur, appropriate
     * error responses are returned.
     * </p>
     *
     * @param request The HTTP request containing the {@code category} query parameter.
     * @param response The HTTP response that will be sent back to the client.
     * @throws ServletException If an error occurs while handling the request.
     * @throws IOException If an error occurs while reading the request or writing the response.
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
