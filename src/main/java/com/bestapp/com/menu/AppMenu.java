package com.bestapp.com.menu;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.controller.ProductController;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.view.ConsoleView;
import lombok.RequiredArgsConstructor;

/**
 * Class responsible for displaying the application menu and handling user interactions.
 * This class presents various options to the user, including login/logout, product management,
 * and cache statistics. Based on user input, it delegates tasks to the appropriate controllers.
 */
@RequiredArgsConstructor
public class AppMenu {

    private final AuthController authController;
    private final ProductController productController;
    private final ConsoleView consoleView;
    private final Metrics metrics;
    private final AuditLogger auditLogger;
    private final AuthService authService;

    private static final String PRODUCT_CATALOG_MENU = """
            
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
            """;

    /**
     * Starts the main application menu loop, displaying available options and handling
     * user input. This method continually prompts the user for input until the user
     * chooses to exit the application.
     */
    public void start() {
        boolean running = true;

        consoleView.showMessage("Welcome to Product Catalog Application!");

        while (running) {
            System.out.println(PRODUCT_CATALOG_MENU);
            String choice = consoleView.read("Choose an option: ");
            switch (choice) {
                case "1" -> authController.login();
                case "2" -> authController.logout();
                case "3" -> { if (authController.requireLogin()) productController.addProduct(); }
                case "4" -> { if (authController.requireLogin()) productController.viewProducts(); }
                case "5" -> {
                    if (authController.requireLogin()) {
                        metrics.start();
                        productController.searchProducts();
                        consoleView.showMessage("Search completed in " + metrics.getElapsedTime() + " ms.");
                    }
                }
                case "6" -> { if (authController.requireLogin()) productController.updateProduct(); }
                case "7" -> { if (authController.requireLogin()) productController.deleteProduct(); }
                case "8" -> { if (authController.requireLogin()) productController.showCacheStats(); }
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
