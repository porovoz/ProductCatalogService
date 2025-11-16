package com.bestapp.ProductCatalog.controller;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.controller.ProductController;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.model.Product;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.ProductService;
import com.bestapp.com.view.ConsoleView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private ProductService productService;
    private ConsoleView consoleView;
    private AuditLogger auditLogger;
    private AuthService authService;
    private Metrics metrics;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        consoleView = mock(ConsoleView.class);
        auditLogger = mock(AuditLogger.class);
        authService = mock(AuthService.class);
        metrics = mock(Metrics.class);

        productController = new ProductController(productService, consoleView, auditLogger, authService, metrics);
    }

    @Test
    void testAddProduct() {
        when(consoleView.readNonEmptyText("Enter product name: ")).thenReturn("Laptop");
        when(consoleView.read("Enter product description: ")).thenReturn("Gaming Laptop");
        when(consoleView.readPositiveDouble("Enter product price: ")).thenReturn(1500.0);
        when(consoleView.read("Enter product category: ")).thenReturn("Electronics");
        when(consoleView.read("Enter product brand: ")).thenReturn("Dell");
        when(consoleView.readPositiveInt("Enter product quantity: ")).thenReturn(10);
        when(authService.getCurrentUser()).thenReturn("user1");

        productController.addProduct();

        verify(productService).addProduct(any(Product.class));
        verify(auditLogger).log("user1 added a new product.");
        verify(consoleView).showMessage("Product added successfully.");
    }

    @Test
    void testViewProducts() {
        Product p = new Product("Phone", "Smartphone", 700, "Electronics", "Samsung", 5);
        when(productService.getAllProducts()).thenReturn(List.of(p));
        when(metrics.getProductCount(productService)).thenReturn(1);

        productController.viewProducts();

        verify(consoleView).showProducts(List.of(p));
        verify(consoleView).showMessage("Total number of products: 1");
    }

    @Test
    void testSearchProductsByCategory() {
        when(consoleView.read("""
                1. By Category
                2. By Brand
                3. By Price Range
                Choose: """)).thenReturn("1");
        when(consoleView.readNonEmptyText("Enter product category: ")).thenReturn("Electronics");
        Product p = new Product("TV", "LED TV", 1200, "Electronics", "Samsung", 3);
        when(productService.getByCategory("Electronics")).thenReturn(List.of(p));

        productController.searchProducts();

        verify(consoleView).showProducts(List.of(p));
    }

    @Test
    void testUpdateProduct() {
        when(consoleView.read("Enter product ID to update: ")).thenReturn("1");
        when(productService.existsById(1L)).thenReturn(true);
        when(consoleView.read("Enter new product name: ")).thenReturn("Updated Laptop");
        when(consoleView.read("Enter new product description: ")).thenReturn("High-end Laptop");
        when(consoleView.readPositiveDouble("Enter new product price: ")).thenReturn(2000.0);
        when(consoleView.read("Enter new product category: ")).thenReturn("Electronics");
        when(consoleView.read("Enter new product brand: ")).thenReturn("Dell");
        when(consoleView.readPositiveInt("Enter new product quantity: ")).thenReturn(5);
        when(authService.getCurrentUser()).thenReturn("user1");

        productController.updateProduct();

        verify(productService).updateProductById(eq(1L), any(Product.class));
        verify(auditLogger).log("user1 updated the product with ID: 1");
        verify(consoleView).showMessage("Product updated successfully.");
    }

    @Test
    void testDeleteProduct() {
        when(consoleView.read("Enter product ID to delete: ")).thenReturn("2");
        when(productService.existsById(2L)).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn("user1");

        productController.deleteProduct();

        verify(productService).removeProductById(2L);
        verify(auditLogger).log("user1 deleted the product with ID: 2");
        verify(consoleView).showMessage("Product deleted successfully.");
    }

}
