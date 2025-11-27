package com.bestapp.ProductCatalog.service;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("productCatalogService")
                    .withUsername("alex")
                    .withPassword("alexSecret");

    private ProductServiceImpl productService;

    @BeforeAll
    void setup() {
        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());

        ProductRepository repository = new ProductRepository(new DatabaseConfig());
        productService = new ProductServiceImpl(repository);
    }

    @BeforeEach
    void clearCache() {
        productService.getCache().clearAll();
    }

    @Test
    void testAddAndGetProduct() {
        Product product = new Product("Test Bike", "Test Description", 500.0, "Bicycles", "TestBrand", 5);
        productService.addProduct(product);

        assertNotNull(product.getId());

        List<Product> products = productService.getAllProducts();
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product.getId())));
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product("Update Bike", "Desc", 300.0, "Bicycles", "BrandA", 10);
        productService.addProduct(product);

        product.setPrice(350.0);
        productService.updateProductById(product.getId(), product);

        List<Product> products = productService.getAllProducts();
        Product updated = products.stream().filter(p -> p.getId().equals(product.getId())).findFirst().orElse(null);

        assertNotNull(updated);
        assertEquals(350.0, updated.getPrice());
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product("Delete Bike", "Desc", 200.0, "Bicycles", "BrandB", 7);
        productService.addProduct(product);

        productService.removeProductById(product.getId());

        List<Product> products = productService.getAllProducts();
        assertFalse(products.stream().anyMatch(p -> p.getId().equals(product.getId())));
    }

    @Test
    void testGetByCategoryAndBrand() {
        Product product1 = new Product("CatBike", "Desc", 100.0, "Bicycles", "BrandC", 5);
        Product product2 = new Product("CatMouse", "Desc", 50.0, "Electronics", "BrandC", 10);
        productService.addProduct(product1);
        productService.addProduct(product2);

        List<Product> bicycles = productService.getByCategory("Bicycles");
        List<Product> brandC = productService.getByBrand("BrandC");

        assertTrue(bicycles.stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertTrue(brandC.stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertTrue(brandC.stream().anyMatch(p -> p.getId().equals(product2.getId())));
    }

    @Test
    void testGetByPriceRange() {
        Product cheapProduct = new Product("Cheap", "Desc", 10.0, "Misc", "BrandX", 2);
        Product expensiveProduct = new Product("Expensive", "Desc", 1000.0, "Misc", "BrandY", 1);
        productService.addProduct(cheapProduct);
        productService.addProduct(expensiveProduct);

        List<Product> midRange = productService.getByPriceRange(5.0, 500.0);

        assertTrue(midRange.stream().anyMatch(p -> p.getId().equals(cheapProduct.getId())));
        assertFalse(midRange.stream().anyMatch(p -> p.getId().equals(expensiveProduct.getId())));
    }

    @Test
    void testCacheBehavior() {
        Product product = new Product("CacheTest", "Desc", 100.0, "Test", "BrandZ", 3);
        productService.addProduct(product);

        List<Product> firstCall = productService.getAllProducts();
        assertEquals(1, productService.getCache().getCacheMisses());

        List<Product> secondCall = productService.getAllProducts();
        assertEquals(1, productService.getCache().getCacheHits());
    }

    @Test
    void testExistsById() {
        Product product = new Product("ExistTest", "Desc", 150.0, "Test", "BrandT", 4);
        productService.addProduct(product);

        assertTrue(productService.existsById(product.getId()));
        assertFalse(productService.existsById(-1L));
    }

}
