package com.bestapp.com.controller;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.model.Product;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.ProductService;
import com.bestapp.com.view.ConsoleView;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Controller responsible for handling all product-related user interactions.
 * Delegates business logic to {@link ProductService} and handles input/output via {@link ConsoleView}.
 */
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ConsoleView consoleView;
    private final AuditLogger auditLogger;
    private final AuthService authService;
    private final Metrics metrics;

    /**
     * Prompts the user for product details and adds a new product to the catalog.
     */
    public void addProduct() {
        String name = consoleView.readNonEmptyText("Enter product name: ");
        String desc = consoleView.read("Enter product description: ");
        double price = consoleView.readPositiveDouble("Enter product price: ");
        String cat = consoleView.read("Enter product category: ");
        String brand = consoleView.read("Enter product brand: ");
        int qty = consoleView.readPositiveInt("Enter product quantity: ");
        Product p = new Product(name, desc, price, cat, brand, qty);
        productService.addProduct(p);
        auditLogger.log(authService.getCurrentUser() + " added a new product.");
        consoleView.showMessage("Product added successfully.");
    }

    /**
     * Displays all products currently stored in the catalog.
     */
    public void viewProducts() {
        List<Product> products = productService.getAllProducts();
        consoleView.showProducts(products);
        consoleView.showMessage("Total number of products: " + metrics.getProductCount(productService));
    }

    /**
     * Allows the user to search for products by category, brand, or price range.
     */
    public void searchProducts() {
        String choice = consoleView.read("""
                1. By Category
                2. By Brand
                3. By Price Range
                Choose: """);
        switch (choice) {
            case "1" -> consoleView.showProducts(productService.getByCategory(consoleView.readNonEmptyText("Enter product category: ")));
            case "2" -> consoleView.showProducts(productService.getByBrand(consoleView.readNonEmptyText("Enter product brand: ")));
            case "3" -> {
                double min = consoleView.readPositiveDouble("Enter product min price: ");
                double max = consoleView.readPositiveDouble("Enter product max price: ");
                if (min > max) {
                    consoleView.showMessage("Min price cannot be greater than max price.");
                    return;
                }
                consoleView.showProducts(productService.getByPriceRange(min, max));
            }
            default -> consoleView.showMessage("Invalid choice.");
        }

    }

    /**
     * Updates an existing product by ID after verifying its existence.
     */
    public void updateProduct() {
        String strId = consoleView.read("Enter product ID to update: ");
        try {
            Long id = Long.parseLong(strId);
            if (!productService.existsById(id)) {
                consoleView.showMessage("Product with ID: " + id + " not found.");
                return;
            }
            String name = consoleView.read("Enter new product name: ");
            String desc = consoleView.read("Enter new product description: ");
            double price = consoleView.readPositiveDouble("Enter new product price: ");
            String cat = consoleView.read("Enter new product category: ");
            String brand = consoleView.read("Enter new product brand: ");
            int qty = consoleView.readPositiveInt("Enter new product quantity: ");
            Product p = new Product(name, desc, price, cat, brand, qty);
            productService.updateProductById(id, p);
            auditLogger.log(authService.getCurrentUser() + " updated the product with ID: " + id);
            consoleView.showMessage("Product updated successfully.");
        } catch (NumberFormatException e) {
            consoleView.showMessage("Invalid ID format.");
        }
    }

    /**
     * Deletes a product by ID after verifying that it exists.
     */
    public void deleteProduct() {
        String strId = consoleView.read("Enter product ID to delete: ");
        try {
            Long id = Long.parseLong(strId);
            if (!productService.existsById(id)) {
                consoleView.showMessage("Product with ID: " + id + " not found.");
                return;
            }
            productService.removeProductById(id);
            auditLogger.log(authService.getCurrentUser() + " deleted the product with ID: " + id);
            consoleView.showMessage("Product deleted successfully.");
        } catch (NumberFormatException e) {
            consoleView.showMessage("Invalid ID format.");
        }
    }

}
