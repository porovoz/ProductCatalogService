package com.bestapp.com.service;

import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
@Testcontainers
@Transactional
class ProductServiceImplTest {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Container
    public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("productCatalogService")
            .withUsername("alex")
            .withPassword("alexSecret");

    private CreateOrUpdateProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new CreateOrUpdateProductDTO("Test Product", "Test Description", 100.0, "Test Category", "Test Brand", 500);
    }

    @Test
    @DisplayName("Create product")
    void testCreateProduct() {
        ProductDTO createdProductDTO = productService.createProduct(productDTO);

        assertThat(createdProductDTO).isNotNull();
        assertThat(createdProductDTO.getName()).isEqualTo("Test Product");
        assertThat(createdProductDTO.getCategory()).isEqualTo("Test Category");
        assertThat(createdProductDTO.getPrice()).isEqualTo(100.0);
        assertThat(createdProductDTO.getBrand()).isEqualTo("Test Brand");

        Product product = productRepository.findById(createdProductDTO.getId()).orElseThrow();
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getCategory()).isEqualTo("Test Category");
        assertThat(product.getPrice()).isEqualTo(100.0);
        assertThat(product.getBrand()).isEqualTo("Test Brand");
    }

    @Test
    @DisplayName("Find all products")
    void testFindAllProducts() {
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product1", "Test Description1", 50.0, "Test Category1", "Test Brand1", 100));
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product2", "Test Description2", 75.0, "Test Category2", "Test Brand2", 500));
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product3", "Test Description3", 100.0, "Test Category3", "Test Brand3", 200));

        List<ProductDTO> products = productService.findAllProducts(1, 10);

        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Get products by category")
    void testGetProductsByCategory() {

        productService.createProduct(new CreateOrUpdateProductDTO("Test Product1", "Test Description1", 50.0, "Test Category1", "Test Brand1", 100));
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product2", "Test Description2", 75.0, "Test Category2", "Test Brand2", 500));

        List<ProductDTO> products = productService.getProductsByCategory("Test Category1");

        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Get products by brand")
    void testGetProductsByBrand() {
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product1", "Test Description1", 50.0, "Test Category1", "Test Brand1", 100));
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product2", "Test Description2", 75.0, "Test Category2", "Test Brand2", 500));

        List<ProductDTO> products = productService.getProductsByBrand("Test Brand1");

        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Get products by price range")
    void testGetProductsByPriceRange() {
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product1", "Test Description1", 50.0, "Test Category1", "Test Brand1", 100));
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product2", "Test Description2", 75.0, "Test Category2", "Test Brand2", 500));
        productService.createProduct(new CreateOrUpdateProductDTO("Test Product3", "Test Description3", 100.0, "Test Category3", "Test Brand3", 200));

        List<ProductDTO> products = productService.getProductsByPriceRange(60.0, 80.0);

        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Update product")
    void testUpdateProduct() {
        ProductDTO createdProductDTO = productService.createProduct(productDTO);

        CreateOrUpdateProductDTO updatedDTO = new CreateOrUpdateProductDTO("Updated Product", "Updated Description", 10.0, "Updated Category", "Updated Brand", 150);
        ProductDTO updatedProductDTO = productService.updateProduct(createdProductDTO.getId(), updatedDTO);

        assertThat(updatedProductDTO).isNotNull();
        assertThat(updatedProductDTO.getName()).isEqualTo("Updated Product");
        assertThat(updatedProductDTO.getCategory()).isEqualTo("Updated Category");
        assertThat(updatedProductDTO.getPrice()).isEqualTo(150.0);
        assertThat(updatedProductDTO.getBrand()).isEqualTo("Updated Brand");
    }

    @Test
    @DisplayName("Delete product by id")
    void testDeleteProduct() {
        ProductDTO createdProductDTO = productService.createProduct(productDTO);

        productService.deleteProductById(createdProductDTO.getId());

        assertThat(productRepository.findById(createdProductDTO.getId())).isEmpty();
    }
}
