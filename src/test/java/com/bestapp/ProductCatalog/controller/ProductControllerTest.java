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
    void setup() {
        productService = mock(ProductService.class);
        consoleView = mock(ConsoleView.class);
        auditLogger = mock(AuditLogger.class);
        authService = mock(AuthService.class);
        metrics = mock(Metrics.class);

        when(authService.getCurrentUser()).thenReturn("admin");

        productController = new ProductController(productService, consoleView, auditLogger, authService, metrics);
    }

    @Test
    void addProductWorks() {
        when(consoleView.read(any())).thenReturn("bicycle", "road bicycle", "bicycles", "BMW");
        when(consoleView.readPositiveDouble(any())).thenReturn(25.0);
        when(consoleView.readPositiveInt(any())).thenReturn(17);

        productController.addProduct();

        verify(productService).addProduct(any(Product.class));
    }

    @Test
    void updateNonExistingShowsError() {
        when(consoleView.read(any())).thenReturn("id1");
        when(productService.existsById("id1")).thenReturn(false);

        productController.updateProduct();

        verify(consoleView).showMessage("Product with ID: id1 not found.");
    }

    @Test
    void updateProductWorks() {
        when(consoleView.read(any())).thenReturn("id1", "bag", "cool bag", "bags", "Gucci");
        when(consoleView.readPositiveDouble(any())).thenReturn(16.0);
        when(consoleView.readPositiveInt(any())).thenReturn(7);

        when(productService.existsById("id1")).thenReturn(true);

        productController.updateProduct();

        verify(productService).updateProductById(eq("id1"), any(Product.class));
    }

    @Test
    void searchByCategory() {
        when(consoleView.read(any())).thenReturn("1");
        when(consoleView.readNonEmptyText(any())).thenReturn("book");

        productController.searchProducts();

        verify(productService).getByCategory("book");
    }

    @Test
    void searchByBrand() {
        when(consoleView.read(any())).thenReturn("2");
        when(consoleView.readNonEmptyText(any())).thenReturn("BMW");

        productController.searchProducts();

        verify(productService).getByBrand("BMW");
    }

    @Test
    void searchByPriceRange() {
        when(consoleView.read(any())).thenReturn("3");
        when(consoleView.readPositiveDouble(any())).thenReturn(15.0, 65.0);

        productController.searchProducts();

        verify(productService).getByPriceRange(15.0, 65.0);
    }

    @Test
    void searchPriceRangeInvalidMinMoreThanMax() {
        when(consoleView.read(any())).thenReturn("3");
        when(consoleView.readPositiveDouble(any())).thenReturn(45.0, 11.0);

        productController.searchProducts();

        verify(consoleView).showMessage("Min price cannot be greater than max price.");
        verify(productService, never()).getByPriceRange(anyDouble(), anyDouble());
    }

}
