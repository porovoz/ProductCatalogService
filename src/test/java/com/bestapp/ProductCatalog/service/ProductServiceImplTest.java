package com.bestapp.ProductCatalog.service;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private AuditLogger auditLogger;
    private AuthService authService;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        auditLogger = mock(AuditLogger.class);
        authService = mock(AuthService.class);

        when(authService.getCurrentUser()).thenReturn("admin");

        productService = new ProductServiceImpl(productRepository, auditLogger, authService);
    }

    @Test
    void addProductSavesToRepo() {
        Product p = new Product("car", "sports car", 125000, "automotive", "Audi", 5);

        productService.addProduct(p);

        verify(productRepository).save(p);
        verify(auditLogger).log("admin added product: car");
    }

    @Test
    void updateProductCallsRepository() {
        Product newP = new Product("pen", "high quality pen", 100, "office supplies", "parker", 7);
        productService.updateProductById("id1", newP);

        verify(productRepository).updateById("id1", newP);
    }

    @Test
    void updateNonExistingThrows() {
        doThrow(new IllegalArgumentException("not found"))
                .when(productRepository).updateById(eq("bad ID"), any());

        assertThrows(IllegalArgumentException.class, () ->
                productService.updateProductById("bad ID", new Product()));
    }

    @Test
    void deleteProductCallsRepo() {
        productService.removeProductById("id1");
        verify(productRepository).deleteById("id1");
    }

    @Test
    void getAllProductsDelegatesToRepo() {
        when(productRepository.findAll()).thenReturn(List.of(new Product()));
        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    void existsByIdDelegates() {
        when(productRepository.existsById("123")).thenReturn(true);
        assertTrue(productService.existsById("123"));
    }

}
