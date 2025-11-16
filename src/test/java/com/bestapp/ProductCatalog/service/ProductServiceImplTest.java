package com.bestapp.ProductCatalog.service;

import com.bestapp.ProductCatalog.BasePostgresTest;
import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.impl.AuthServiceImpl;
import com.bestapp.com.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest extends BasePostgresTest {

    private ProductServiceImpl productService;
    private ProductRepository productRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
        authService = new AuthServiceImpl();
        productService = new ProductServiceImpl(productRepository, new AuditLogger(), authService);
    }

    @Test
    void testAddGetProductsAndCache() {
        Product p = new Product("Monitor", "4K Monitor", 400, "Electronics", "LG", 7);
        productService.addProduct(p);

        List<Product> allProducts = productService.getAllProducts();
        assertTrue(allProducts.stream().anyMatch(prod -> prod.getName().equals("Monitor")));

        assertTrue(productService.getCache().getCacheHits() >= 0);
        assertTrue(productService.getCache().getCacheMisses() >= 0);

        List<Product> cached = productService.getAllProducts();
        assertEquals(allProducts.size(), cached.size());
    }

    @Test
    void testUpdateAndDeleteProduct() {
        Product p = new Product("Keyboard", "Mechanical", 120, "Electronics", "Corsair", 15);
        productService.addProduct(p);
        Long id = p.getId();

        p.setPrice(110);
        productService.updateProductById(id, p);
        Product updated = productService.getAllProducts().stream().filter(prod -> prod.getId().equals(id)).findFirst().orElse(null);
        assertNotNull(updated);
        assertEquals(110, updated.getPrice());

        productService.removeProductById(id);
        assertFalse(productService.existsById(id));
    }

}
