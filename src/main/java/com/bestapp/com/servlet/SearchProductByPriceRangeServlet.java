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
 * {@code SearchProductByPriceRangeServlet} is a servlet that handles HTTP GET requests to search for products within a specific price range.
 * <p>
 * This servlet listens for requests at the URL pattern {@code /api/products/price-range}. It expects two query parameters:
 * {@code min} (the minimum price) and {@code max} (the maximum price). These parameters are used to filter products within the specified price range.
 * </p>
 * <p>
 * The servlet performs the following tasks:
 * <ul>
 *   <li>Checks if the user is authenticated by inspecting the session. If the user is not logged in, a {@code 401 Unauthorized}
 *       response is returned.</li>
 *   <li>Validates the {@code min} and {@code max} price parameters. If either is missing, empty, or invalid, a {@code 400 Bad Request}
 *       response is returned.</li>
 *   <li>Gets products that fall within the specified price range using {@link ProductService#getByPriceRange(double, double)}.</li>
 *   <li>If products are found, they are returned as a JSON array of {@link ProductDTO} objects with a {@code 200 OK} status.</li>
 *   <li>If no products are found within the price range, a {@code 404 Not Found} response is returned.</li>
 *   <li>If the price format is invalid (non-numeric values for {@code min} or {@code max}), a {@code 400 Bad Request} response is returned.</li>
 *   <li>In case of an internal error (e.g., database issues), a {@code 500 Internal Server Error} response is returned.</li>
 * </ul>
 * </p>
 */
@WebServlet("/api/products/price-range")
public class SearchProductByPriceRangeServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductRepository(new DatabaseConfig()));
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String APPLICATION_JSON = "application/json";
    private static final String NEED_TO_LOGIN = "You need to log in first.";
    private static final String MIN_PRICE_PARAM = "min";
    private static final String MAX_PRICE_PARAM = "max";


    /**
     * Handles the {@code GET} request to search for products within a specific price range.
     * <p>
     * This method retrieves the {@code min} and {@code max} query parameters from the request. If the user is authenticated
     * and the parameters are valid, it gets the products that fall within the specified price range using the
     * {@link ProductService}. The products are returned as a list of {@link ProductDTO} objects in JSON format.
     * If any errors occur, appropriate error responses are returned.
     * </p>
     *
     * @param request The HTTP request containing the {@code min} and {@code max} price parameters.
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

        String minPriceParam = request.getParameter(MIN_PRICE_PARAM);
        String maxPriceParam = request.getParameter(MAX_PRICE_PARAM);
        if (minPriceParam == null || maxPriceParam == null || minPriceParam.trim().isEmpty() || maxPriceParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"Both 'min' and 'max' price parameters are required.\"}");
            return;
        }
        try {
            double minPrice = Double.parseDouble(minPriceParam);
            double maxPrice = Double.parseDouble(maxPriceParam);

            if (minPrice > maxPrice) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(APPLICATION_JSON);
                response.getWriter().write("{\"error\": \"Minimum price cannot be greater than maximum price.\"}");
                return;
            }

            List<Product> products = productService.getByPriceRange(minPrice, maxPrice);
            if (products.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType(APPLICATION_JSON);
                response.getWriter().write("{\"error\": \"No products found in this price range.\"}");
                return;
            }

            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductMapper.INSTANCE::productToProductDTO)
                    .toList();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write(objectMapper.writeValueAsString(productDTOs));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"Invalid price format. Please provide valid numeric values for 'min' and 'max' prices.\"}");
        } catch (Exception e) {
            log("Error getting products by price range: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"An unexpected error occurred while getting products by price range.\"}");
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
