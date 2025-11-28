package com.bestapp.ProductCatalog.service;

import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private CreateOrUpdateProductDTO createOrUpdateProductDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createOrUpdateProductDTO = new CreateOrUpdateProductDTO();
        createOrUpdateProductDTO.setName("Product1");
        createOrUpdateProductDTO.setDescription("Description");
        createOrUpdateProductDTO.setPrice(100.0);
        createOrUpdateProductDTO.setCategory("Category");
        createOrUpdateProductDTO.setBrand("Brand");
        createOrUpdateProductDTO.setStockQuantity(10);
    }

    @Test
    @DisplayName("Create product should return ProductDTO")
    void createProductShouldReturnProductDTO() {
        Product savedProduct = new Product("Product1", "Description", 100.0, "Category", "Brand", 10);
        savedProduct.setId(1L);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ProductDTO result = productService.createProduct(createOrUpdateProductDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Product1", result.getName());
        assertEquals("Brand", result.getBrand());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product by id should delete product")
    void deleteProductByIdShouldDeleteProduct() {
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        productService.deleteProductById(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Update product should return updated ProductDTO")
    void updateProductShouldReturnUpdatedProductDTO() {
        Long productId = 1L;
        Product existingProduct = new Product("Product1", "Description", 100.0, "Category", "Brand", 10);
        existingProduct.setId(productId);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result = productService.updateProduct(productId, createOrUpdateProductDTO);

        assertNotNull(result);
        assertEquals("Product1", result.getName());
        assertEquals("Brand", result.getBrand());
        assertEquals(100.0, result.getPrice());

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Find all products should return product DTO list")
    void findAllProductsShouldReturnProductDTOList() {
        Product product = new Product("Product1", "Description", 100.0, "Category", "Brand", 10);

        List<Product> products = List.of(product);
        PageRequest pageRequest = PageRequest.of(0, 50);
        Page<Product> page = new PageImpl<>(products, pageRequest, products.size());

        when(productRepository.findAll(pageRequest)).thenReturn(page);

        List<ProductDTO> result = productService.findAllProducts(1, 50);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product1", result.get(0).getName());

        verify(productRepository, times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("Get products by category should return product DTO list")
    void getProductsByCategoryShouldReturnProductDTOList() {
        String category = "Category1";
        Product product = new Product("Product1", "Description", 100.0, category, "Brand", 10);

        when(productRepository.findByCategoryIgnoreCase(category))
                .thenReturn(List.of(product));

        List<ProductDTO> result = productService.getProductsByCategory(category);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product1", result.get(0).getName());

        verify(productRepository, times(1))
                .findByCategoryIgnoreCase(category);
    }

    @Test
    @DisplayName("Get products by brand should return product DTO list")
    void getProductsByBrandShouldReturnProductDTOList() {
        String brand = "Brand1";
        Product product = new Product("Product1", "Description", 100.0, "Category", brand, 10);

        when(productRepository.findByBrandIgnoreCase(brand))
                .thenReturn(List.of(product));

        List<ProductDTO> result = productService.getProductsByBrand(brand);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product1", result.get(0).getName());

        verify(productRepository, times(1))
                .findByBrandIgnoreCase(brand);
    }

    @Test
    @DisplayName("Get products by price range should return product DTO list")
    void getProductsByPriceRangeShouldReturnProductDTOList() {
        double min = 50.0;
        double max = 150.0;
        Product product = new Product("Product1", "Description", 100.0, "Category", "Brand", 10);

        when(productRepository.findByPriceBetween(min, max))
                .thenReturn(List.of(product));

        List<ProductDTO> result = productService.getProductsByPriceRange(min, max);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product1", result.get(0).getName());

        verify(productRepository, times(1))
                .findByPriceBetween(min, max);
    }

    @Test
    @DisplayName("Exists by id should return true")
    void existsByIdShouldReturnTrue() {
        Long id = 1L;
        when(productRepository.existsById(id)).thenReturn(true);

        boolean result = productService.existsById(id);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(id);
    }
}
