package com.bestapp.ProductCatalog.repository;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("productCatalogService")
                    .withUsername("alex")
                    .withPassword("alexSecret");

    private ProductRepository productRepository;

    @BeforeAll
    void setup() {
        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());

        DatabaseConfig databaseConfig = new DatabaseConfig();
        productRepository = new ProductRepository(databaseConfig);
    }

    @Test
    void testSaveAndFindById() {
        Product product = new Product("Test Bike", "Mountain bike", 499.99, "Bicycles", "BikeBrand", 5);
        productRepository.save(product);

        assertNotNull(product.getId());

        List<Product> allProducts = productRepository.findAll();
        assertTrue(allProducts.stream().anyMatch(p -> p.getId().equals(product.getId())));
    }

    @Test
    void testUpdateProduct() {
        Product p = new Product("OldName", "OldDesc", 50.0, "Misc", "BrandB", 3);
        productRepository.save(p);

        p.setName("NewName");
        productRepository.updateById(p.getId(), p);

        Product updated = productRepository.findAll().stream()
                .filter(prod -> prod.getId().equals(p.getId()))
                .findFirst().orElse(null);

        assertNotNull(updated);
        assertEquals("NewName", updated.getName());
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product("DeleteMe", "Desc", 50, "Cat", "Brand", 1);
        productRepository.save(product);
        Long id = product.getId();

        productRepository.deleteById(id);

        assertFalse(productRepository.existsById(id));
    }

    @Test
    void testFindByCategoryAndBrand() {
        Product p1 = new Product("Bike1", "Desc", 100, "Bicycles", "BrandX", 1);
        Product p2 = new Product("Bike2", "Desc", 150, "Bicycles", "BrandY", 2);
        productRepository.save(p1);
        productRepository.save(p2);

        List<Product> byCategory = productRepository.findByCategory("Bicycles");
        assertTrue(byCategory.stream().anyMatch(p -> p.getId().equals(p1.getId())));
        assertTrue(byCategory.stream().anyMatch(p -> p.getId().equals(p2.getId())));

        List<Product> byBrand = productRepository.findByBrand("BrandX");
        assertTrue(byBrand.stream().anyMatch(p -> p.getId().equals(p1.getId())));
    }

    @Test
    void testFindByPriceRange() {
        Product cheap = new Product("Cheap", "Desc", 10.0, "Misc", "BrandZ", 1);
        Product expensive = new Product("Expensive", "Desc", 1000.0, "Misc", "BrandZ", 1);
        productRepository.save(cheap);
        productRepository.save(expensive);

        List<Product> midRange = productRepository.findByPriceRange(5, 500);
        assertTrue(midRange.stream().anyMatch(p -> p.getId().equals(cheap.getId())));
        assertFalse(midRange.stream().anyMatch(p -> p.getId().equals(expensive.getId())));
    }
}
