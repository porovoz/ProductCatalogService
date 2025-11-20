package com.bestapp.com.factory;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.controller.ProductController;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.impl.AuthServiceImpl;
import com.bestapp.com.service.impl.ProductServiceImpl;
import com.bestapp.com.view.ConsoleView;

/**
 * A factory class for creating various components of the application such as controllers, services, and repositories.
 * This factory encapsulates the creation of dependencies and provides convenient methods for retrieving controllers and services.
 *
 * <p>The main components created by this factory are:</p>
 * <ul>
 *   <li>{@link ProductController} - controller for working with products;</li>
 *   <li>{@link AuthController} - controller for authentication;</li>
 *   <li>{@link ProductServiceImpl} - service for managing products;</li>
 *   <li>{@link AuthServiceImpl} - service for user authentication;</li>
 *   <li>{@link ConsoleView} - view for displaying data to the console;</li>
 *   <li>{@link AuditLogger} - logger for tracking actions in the application;</li>
 *   <li>{@link Metrics} - metrics for monitoring the application's performance;</li>
 *   <li>{@link ProductRepository} - repository for working with product data;</li>
 *   <li>{@link DatabaseConfig} - configuration for the application's database.</li>
 * </ul>
 */
public class AppFactory {

    private final AuditLogger auditLogger = new AuditLogger();
    private final AuthServiceImpl authService = new AuthServiceImpl();
    private final ConsoleView consoleView = new ConsoleView();
    private final Metrics metrics = new Metrics();
    private final DatabaseConfig databaseConfig = new DatabaseConfig();
    private final ProductRepository productRepository = new ProductRepository(databaseConfig);

    /**
     * Creates and returns a new instance of {@link ProductController}.
     * The controller for managing products, which depends on the product service, console view,
     * audit logger, authentication service, and metrics.
     *
     * @return a new {@link ProductController} instance.
     */
    public ProductController createProductController() {
        ProductServiceImpl productService = new ProductServiceImpl(productRepository);

        return new ProductController(
                productService,
                consoleView,
                auditLogger,
                authService,
                metrics
        );
    }

    /**
     * Creates and returns a new instance of {@link AuthController}.
     * The controller for user authentication, which depends on the authentication service,
     * console view, and audit logger.
     *
     * @return a new {@link AuthController} instance.
     */
    public AuthController createAuthController() {
        return new AuthController(
                authService,
                consoleView,
                auditLogger
        );
    }

    /**
     * Returns the {@link ConsoleView} instance used for displaying information to the console.
     *
     * @return the {@link ConsoleView} instance.
     */
    public ConsoleView getConsoleView() {
        return consoleView;
    }

    /**
     * Returns the {@link Metrics} instance used for monitoring and collecting application metrics.
     *
     * @return the {@link Metrics} instance.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Returns the {@link AuthServiceImpl} instance responsible for user authentication.
     *
     * @return the {@link AuthServiceImpl} instance.
     */
    public AuthServiceImpl getAuthService() {
        return authService;
    }

    /**
     * Returns the {@link AuditLogger} instance used for logging and tracking actions in the application.
     *
     * @return the {@link AuditLogger} instance.
     */
    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

}
