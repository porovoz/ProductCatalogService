package com.bestapp.com;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.cache.ProductCache;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.controller.ProductController;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.impl.AuthServiceImpl;
import com.bestapp.com.service.impl.ProductServiceImpl;
import com.bestapp.com.view.ConsoleView;


/**
 * Entry point of the Product Catalog Application.
 * <p>
 * This class initializes all dependencies (manual dependency injection),
 * loads saved products, and provides an interactive console-based menu
 * for managing users and products.
 * </p>
 */
public class Main {

    /**
     * Starts the Product Catalog console application.
     */
    public static void main(String[] args) {
        AuditLogger auditLogger = new AuditLogger();
        ProductRepository productRepository = new ProductRepository();
        AuthServiceImpl authService = new AuthServiceImpl();
        ProductServiceImpl productService = new ProductServiceImpl(productRepository);
        ConsoleView consoleView = new ConsoleView();
        Metrics metrics = new Metrics();
        ProductController productController = new ProductController(productService, consoleView, auditLogger, authService, metrics);
        AuthController authController = new AuthController(authService, consoleView, auditLogger);

        boolean running = true;
        consoleView.showMessage("Welcome to Product Catalog Application!");
        while (running) {
            System.out.println("""
                    
                    === PRODUCT CATALOG MENU ===
                    1. Login
                    2. Logout
                    3. Add product
                    4. View products
                    5. Search products
                    6. Update product
                    7. Remove product
                    8. Show cache stats
                    9. Exit
                    """);
            String choice = consoleView.read("Choose an option: ");
            switch (choice) {
                case "1" -> authController.login();
                case "2" -> authController.logout();
                case "3" -> {
                    if (authController.requireLogin()) {
                        productController.addProduct();
                    }
                }
                case "4" -> {
                    if (authController.requireLogin()) {
                        productController.viewProducts();
                    }
                }
                case "5" -> {
                    if (authController.requireLogin()) {
                    metrics.start();
                    productController.searchProducts();
                    consoleView.showMessage("Search completed in " + metrics.getElapsedTime() + " ms.");
                    }
                }
                case "6" -> {
                    if (authController.requireLogin()) {
                        productController.updateProduct();
                    }
                }
                case "7" -> {
                    if (authController.requireLogin()) {
                        productController.deleteProduct();
                    }
                }
                case "8" -> {
                    if (authController.requireLogin()) {
                        ProductCache cache = productService.getCache();
                        consoleView.showMessage("Cache hits: " + cache.getCacheHits() +
                                ", Cache misses: " + cache.getCacheMisses());
                    }
                }
                case "9" -> {
                    consoleView.showMessage("Goodbye!");
                    auditLogger.log("Application exited by " + authService.getCurrentUser());
                    running = false;
                }
                default -> consoleView.showMessage("Invalid menu option! Try again.");
            }
        }
    }

}