package com.bestapp.com;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.auth.UserAuth;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.model.Product;
import com.bestapp.com.model.ProductCatalog;
import com.bestapp.com.persistence.ProductPersistence;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point of the application.
 * Provides a console menu for managing products and user authentication.
 */
public class Main {

    private static final String NEED_TO_LOGIN = "You need to log in first.";

    private final ProductCatalog productCatalog = new ProductCatalog();
    private final UserAuth userAuth = new UserAuth();
    private final AuditLogger auditLogger = new AuditLogger();
    private final Metrics metrics = new Metrics();
    private final Scanner scanner = new Scanner(System.in);

    public Main() {

        /*
         * Starts the main interactive loop.
         * The loop terminates when user selects the Exit option.
         */
        List<Product> loaded = ProductPersistence.loadProducts();
        loaded.forEach(productCatalog::addProduct);
    }


    /**
     * Main program loop with menu navigation.
     */
    public void start() {
        boolean running = true;

         while (running) {
             System.out.println("\n=== PRODUCT CATALOG MENU ===");
             System.out.println("1. Login");
             System.out.println("2. Logout");
             System.out.println("3. Add product");
             System.out.println("4. View products");
             System.out.println("5. Search products");
             System.out.println("6. Update product");
             System.out.println("7. Remove product");
             System.out.println("8. Show cache stats");
             System.out.println("9. Exit");
             System.out.print("Choose an option: ");
             String choice = scanner.nextLine();

             switch (choice) {
                 case "1" -> login();
                 case "2" -> logout();
                 case "3" -> addProduct();
                 case "4" -> viewProducts();
                 case "5" -> searchProducts();
                 case "6" -> updateProduct();
                 case "7" -> removeProduct();
                 case "8" -> showCacheStats();
                 case "9" -> {
                     running = false;
                     exit();
                 }
                 default -> System.out.println("Invalid option. Try again.");
             }
         }
    }

    /**
     * Handles user login input and authentication.
     */
    private void login() {
        if (userAuth.getLoggedInUser() != null) {
            System.out.println("Already logged in as: " + userAuth.getLoggedInUser());
            return;
        }
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        if (userAuth.login(username, password)) {
            auditLogger.log(userAuth.getLoggedInUser() + " logged in");
        }
    }

    /**
     * Logs out the current user.
     */
    private void logout() {
        if (userAuth.getLoggedInUser() == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        auditLogger.log(userAuth.getLoggedInUser() + " logged out");
        userAuth.logout();
    }

    /**
     * Adds a new product to the catalog after input validation.
     */
    private void addProduct() {
        if (notLogged()) {
            return;
        }
        Product product = readProductDetails();
        productCatalog.addProduct(product);
        auditLogger.log(userAuth.getLoggedInUser() + " added product: " + product.getName());
        System.out.println("Product added successfully.");
    }

    /**
     * Displays all products in the catalog.
     */
    private void viewProducts() {
        if (notLogged()) {
            return;
        }
        List<Product> products = productCatalog.getProducts();
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        System.out.println("\n--- PRODUCTS ---");
        products.forEach(this::printProduct);
        System.out.println("Total number of products: " + metrics.getProductCount(productCatalog));
    }

    /**
     * Allows searching products by category, brand, or price range.
     * Also measures time taken.
     */
    private void searchProducts() {
        if (notLogged()) {
            return;
        }
        System.out.println("1. By Category\n2. By Brand\n3. By Price Range");
        String choice = scanner.nextLine();
        metrics.start();
        List<Product> results = List.of();
        switch (choice) {
            case "1" -> {
                String category = readNonEmptyString("Enter product category: ");
                results = productCatalog.searchByCategory(category);
            }
            case "2" -> {
                String brand = readNonEmptyString("Enter product brand: ");
                results = productCatalog.searchByBrand(brand);
            }
            case "3" -> {
                double min = readPositiveDouble("Enter product min price: ");
                double max = readPositiveDouble("Enter product max price: ");
                results = productCatalog.searchByPriceRange(min, max);
            }
            default -> System.out.println("Invalid choice.");
        }
        if (results.isEmpty()) {
            System.out.println("No products found for the given criteria.");
        } else {
            results.forEach(this::printProduct);
        }
        long elapsedTime = metrics.getElapsedTime();
        System.out.println("Search completed in " + elapsedTime + " milliseconds.");
        System.out.println("Total number of products: " + metrics.getProductCount(productCatalog));
    }

    /**
     * Updates product after verifying id exists. Prompts user for new details.
     */
    private void updateProduct() {
        if (notLogged()) {
            return;
        }
        String id = readNonEmptyString("Enter product ID to update: ");
        if (productCatalog.notExists(id)) {
            System.out.println("Product with ID " + id + " not found.");
            return;
        }
        Product newProduct = readProductDetails();
        productCatalog.updateProductById(id, newProduct);
        auditLogger.log(userAuth.getLoggedInUser() + " updated product with ID: " + id);
        System.out.println("Product updated successfully.");
    }

    /**
     * Removes product by ID after checking existence.
     */
    private void removeProduct() {
        if (notLogged()) {
            return;
        }
        System.out.print("Enter product ID to remove: ");
        String id = readNonEmptyString("Enter product ID to remove: ");
        if (productCatalog.notExists(id)) {
            System.out.println("Product with ID " + id + " not found.");
            return;
        }
        productCatalog.removeProductById(id);
        auditLogger.log(userAuth.getLoggedInUser() + " removed product with ID: " + id);
        System.out.println("Product removed successfully.");
    }

    /**
     * Prints cache hits/misses.
     */
    private void showCacheStats() {
        System.out.println("Cache hits: " + productCatalog.getCache().getCacheHits());
        System.out.println("Cache misses: " + productCatalog.getCache().getCacheMisses());
    }

    /**
     * Displays if user logged in.
     * @return true if user not logged in.
     */
    private boolean notLogged() {
        if (userAuth.getLoggedInUser() == null) {
            System.out.println(NEED_TO_LOGIN);
            return true;
        }
        return false;
    }

    /**
     * Saves data and exits the program gracefully.
     */
    private void exit() {
        ProductPersistence.saveProducts(productCatalog.getAllProductsDirect());
        auditLogger.log("System exited by user " + userAuth.getLoggedInUser());
        System.out.println("Products saved. Goodbye!");
    }

    /**
     * Reads a product’s full details from console input.
     */
    private Product readProductDetails() {
        String name = readNonEmptyString("Enter product name: ");
        String description = readNonEmptyString("Enter product description: ");
        double price = readPositiveDouble("Enter product price: ");
        String category = readNonEmptyString("Enter product category: ");
        String brand = readNonEmptyString("Enter product brand: ");
        int quantity = readPositiveInt("Enter stock quantity: ");
        return new Product(name, description, price, category, brand, quantity);
    }

    /**
     * Helper for formatted product printing.
     */
    private void printProduct(Product p) {
        System.out.printf("ID: %s | Name: %s | Description: %s | Price: %.2f | Category: %s | Brand: %s | Quantity: %d%n",
                p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getCategory(), p.getBrand(), p.getStockQuantity());
    }

    /**
     * Reads a non-empty string from console.
     *
     * @param msg prompt message
     * @return trimmed non-empty string
     */
    private String readNonEmptyString(String msg) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Try again.");
        }
    }

    /**
     * Reads a non-negative double from console.
     *
     * @param msg prompt message
     * @return parsed double >= 0
     */
    private double readPositiveDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                double value = Double.parseDouble(scanner.nextLine());
                if (value >= 0) {
                    return value;
                }
                System.out.println("Value cannot be negative. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    /**
     * Reads a non-negative integer from console.
     *
     * @param msg prompt message
     * @return parsed int >= 0
     */
    private int readPositiveInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
                System.out.println("Value cannot be negative. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer, try again.");
            }
        }
    }

    /**
     * Starts the program.
     */
    public static void main(String[] args) {
        new Main().start();
    }

}