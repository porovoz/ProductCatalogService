package com.bestapp.ProductCatalog.repository;

import com.bestapp.ProductCatalog.BasePostgresTest;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest extends BasePostgresTest {

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
    }

    @Test
    void testSaveAndFindAll() {
        Product product = new Product("Laptop", "Gaming Laptop", 1500.0, "Electronics", "Dell", 10);
        productRepository.save(product);
        assertNotNull(product.getId());

        List<Product> allProducts = productRepository.findAll();
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Laptop")));
    }

//    @Test
//    void testFindByCategoryBrandPrice() {
//        Product p1 = new Product("Phone", "Smartphone", 700, "Electronics", "Samsung", 5);
//        Product p2 = new Product("TV", "LED TV", 1200, "Electronics", "Samsung", 3);
//        productRepository.save(p1);
//        productRepository.save(p2);
//
//        List<Product> byCategory = productRepository.findByCategory("Electronics");
//        assertEquals(2, byCategory.size());
//
//        List<Product> byBrand = productRepository.findByBrand("Samsung");
//        assertEquals(2, byBrand.size());
//
//        List<Product> byPrice = productRepository.findByPriceRange(600, 1000);
//        assertEquals(1, byPrice.size());
//        assertEquals("Phone", byPrice.get(0).getName());
//    }

    @Test
    void testUpdateAndDelete() {
        Product p = new Product("Camera", "DSLR", 800, "Electronics", "Canon", 4);
        productRepository.save(p);
        Long id = p.getId();

        p.setPrice(750);
        productRepository.updateById(id, p);
        Product updated = productRepository.findAll().stream().filter(prod -> prod.getId().equals(id)).findFirst().orElse(null);
        assertNotNull(updated);
        assertEquals(750, updated.getPrice());

        productRepository.deleteById(id);
        assertFalse(productRepository.existsById(id));
    }
}
