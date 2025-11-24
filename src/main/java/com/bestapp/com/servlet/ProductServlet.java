package com.bestapp.com.servlet;

import com.bestapp.com.audit.AuditLogger;
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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code ProductServlet} - A servlet that handles product-related operations such as adding, getting, updating, and deleting products.
 * <p>
 * This servlet listens for HTTP requests at the URL pattern {@code /api/products/*}, and performs the following CRUD operations:
 * <ul>
 *   <li>{@code POST}: Add a new product</li>
 *   <li>{@code GET}: Get all products</li>
 *   <li>{@code PUT}: Update an existing product</li>
 *   <li>{@code DELETE}: Delete a product by ID</li>
 * </ul>
 * <p>
 * Each operation checks if the user is authenticated by inspecting the session. If the user is not logged in, the servlet will return an {@code 401 Unauthorized} response.
 * The servlet uses {@link ProductService} to perform CRUD operations on products and {@link AuditLogger} to log these actions for audit purposes.
 * </p>
 * <p>
 * The responses are in {@code application/json} format and include relevant status codes (e.g., {@code 200 OK}, {@code 400 Bad Request}, {@code 404 Not Found}, etc.)
 * along with appropriate messages.
 * </p>
 */
@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductRepository(new DatabaseConfig()));
    private final AuditLogger auditLogger = new AuditLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String APPLICATION_JSON = "application/json";
    private static final String NEED_TO_LOGIN = "You need to log in first.";
    private static final String PRODUCT_NOT_FOUND = "Product not found.";
    private static final String INVALID_ID_FORMAT = "Invalid ID format.";

    /**
     * Handles the {@code POST} request to add a new product.
     * <p>
     * This method reads the {@link ProductDTO} from the request body, validates the data,
     * and adds the product to the database if valid. It also logs the action using {@link AuditLogger}.
     * If the user is not authenticated, it responds with a {@code 401 Unauthorized} status.
     * </p>
     *
     * @param request The HTTP request containing the product data in the request body.
     * @param response The HTTP response that will be sent back to the client.
     * @throws ServletException If an error occurs while handling the request.
     * @throws IOException If an error occurs while reading the input or writing the response.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = getLoggedUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + NEED_TO_LOGIN + "\"}");
            return;
        }

        try {
            ProductDTO productDTO = objectMapper.readValue(request.getInputStream(), ProductDTO.class);

            Set<ConstraintViolation<ProductDTO>> violations = validator.validate(productDTO);
            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(APPLICATION_JSON);
                String errorMessages = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.joining(", "));
                response.getWriter().write("{\"error\": \"" + errorMessages + "\"}");
                return;
            }

            Product product = ProductMapper.INSTANCE.productDTOToProduct(productDTO);
            productService.addProduct(product);
            auditLogger.log(user + " added a new product.");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Product added successfully.");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid input or malformed JSON.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An error occurred while adding the product.\"}");
        }
    }

    /**
     * Handles the {@code GET} request to get all products.
     * <p>
     * This method get the list of all products from the {@link ProductService}, converts them to {@link ProductDTO} objects,
     * and returns them in the response body in {@code application/json} format. If the user is not authenticated,
     * it responds with a {@code 401 Unauthorized} status.
     * </p>
     *
     * @param request The HTTP request.
     * @param response The HTTP response that will be sent back to the client.
     * @throws ServletException If an error occurs while handling the request.
     * @throws IOException If an error occurs while reading the input or writing the response.
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
            List<Product> products = productService.getAllProducts();
            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductMapper.INSTANCE::productToProductDTO)
                    .toList();
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write(objectMapper.writeValueAsString(productDTOs));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"An error occurred while getting all products.\"}");
        }
    }

    /**
     * Handles the {@code PUT} request to update an existing product.
     * <p>
     * This method reads the {@link ProductDTO} from the request body, validates the data,
     * and updates the corresponding product in the database. If the product does not exist, it returns a {@code 404 Not Found} status.
     * It also logs the action using {@link AuditLogger}.
     * </p>
     *
     * @param request The HTTP request containing the product data to update.
     * @param response The HTTP response that will be sent back to the client.
     * @throws ServletException If an error occurs while handling the request.
     * @throws IOException If an error occurs while reading the input or writing the response.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = getLoggedUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + NEED_TO_LOGIN + "\"}");
            return;
        }

        ProductDTO productDTO;
        try {
            productDTO = objectMapper.readValue(request.getInputStream(), ProductDTO.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"Invalid JSON format\"}");
            return;
        }
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(productDTO);
        if (!violations.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            String errorMessages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            response.getWriter().write("{\"error\": \"" + errorMessages + "\"}");
            return;
        }
        Long productId = productDTO.getId();
        if (!productService.existsById(productId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + PRODUCT_NOT_FOUND + "\"}");
            return;
        }
        Product product = ProductMapper.INSTANCE.productDTOToProduct(productDTO);
        productService.updateProductById(productId, product);
        auditLogger.log(user + " updated a product with ID: " + productId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write("Product updated successfully.");
    }

    /**
     * Handles the {@code DELETE} request to delete a product by ID.
     * <p>
     * This method deletes the specified product by ID. If the product does not exist, it responds with a {@code 404 Not Found} status.
     * If the ID format is invalid, it responds with a {@code 400 Bad Request} status.
     * It also logs the action using {@link AuditLogger}.
     * </p>
     *
     * @param request The HTTP request containing the product ID in the URL path.
     * @param response The HTTP response that will be sent back to the client.
     * @throws ServletException If an error occurs while handling the request.
     * @throws IOException If an error occurs while reading the input or writing the response.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = getLoggedUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + NEED_TO_LOGIN + "\"}");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"Product ID is required\"}");
            return;
        }
        Long productId;
        try {
            productId = Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + INVALID_ID_FORMAT + "\"}");
            return;
        }
        if (!productService.existsById(productId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\": \"" + PRODUCT_NOT_FOUND + "\"}");
            return;
        }
        productService.removeProductById(productId);
        auditLogger.log(user + " deleted the product with ID: " + productId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write("Product deleted successfully.");
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
