package com.bestapp.ProductCatalog.service;

import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductClearsCache() {
        Product product = new Product("P1", "Desc", 10, "Cat", "Brand", 1);

        productService.addProduct(product);

        verify(productRepository, times(1)).save(product);
        assertEquals(0, productService.getCache().getCacheHits());
    }

    @Test
    void testGetAllProductsCaching() {
        Product product = new Product("P1", "Desc", 10, "Cat", "Brand", 1);
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> firstCall = productService.getAllProducts();
        List<Product> secondCall = productService.getAllProducts();

        verify(productRepository, times(1)).findAll();
        assertEquals(1, firstCall.size());
        assertEquals(1, secondCall.size());
        assertEquals(1, productService.getCache().getCacheHits());
    }

    @Test
    void testGetByCategoryCaching() {
        Product product = new Product("P1", "Desc", 10, "Cat", "Brand", 1);
        when(productRepository.findByCategory("Cat")).thenReturn(List.of(product));

        List<Product> firstCall = productService.getByCategory("Cat");
        List<Product> secondCall = productService.getByCategory("Cat");

        verify(productRepository, times(1)).findByCategory("Cat");
        assertEquals(1, firstCall.size());
        assertEquals(1, secondCall.size());
        assertEquals(1, productService.getCache().getCacheHits());
    }

    @Test
    void testRemoveAndUpdateProductClearsCache() {
        Product product = new Product("P1", "Desc", 10, "Cat", "Brand", 1);

        productService.updateProductById(1L, product);
        productService.removeProductById(1L);

        verify(productRepository, times(1)).updateById(1L, product);
        verify(productRepository, times(1)).deleteById(1L);
        assertEquals(0, productService.getCache().getCacheHits());
    }
}
