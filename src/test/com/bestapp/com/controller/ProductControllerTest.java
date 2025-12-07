package com.bestapp.com.controller;

import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    private ProductDTO productDTO;
    private CreateOrUpdateProductDTO createOrUpdateProductDTO;

    @Container
    public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("productCatalogService")
            .withUsername("alex")
            .withPassword("alexSecret");

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO(5L, "TestProduct", "Test Description", 100.0, "Test Category", "Test Brand", 500);
        createOrUpdateProductDTO = new CreateOrUpdateProductDTO("Test Product", "Test Description", 100.0, "Test Category", "Test Brand", 500);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    @DisplayName("Create new product should return created")
    @WithMockUser(roles = "USER")
    void createProduct_ShouldReturnCreated() throws Exception {
        when(productService.createProduct(any(CreateOrUpdateProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Product\",\"category\":\"Test Category\",\"price\":100.0,\"brand\":\"Test Brand\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.category").value("Test Category"))
                .andExpect(jsonPath("$.brand").value("Test Brand"))
                .andExpect(jsonPath("$.stock_quantity").value(500));

        verify(productService, times(1)).createProduct(any(CreateOrUpdateProductDTO.class));
    }

    @Test
    @DisplayName("Get all products should return ok")
    @WithMockUser(roles = "USER")
    void getAllProducts_ShouldReturnOk() throws Exception {
        when(productService.findAllProducts(anyInt(), anyInt())).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].category").value("Test Category"))
                .andExpect(jsonPath("$[0].brand").value("Test Brand"))
                .andExpect(jsonPath("$[0].stock_quantity").value(500));

        verify(productService, times(1)).findAllProducts(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Get products by brand should return ok")
    @WithMockUser(roles = "USER")
    void getProductsByBrand_ShouldReturnOk() throws Exception {

        when(productService.getProductsByBrand(anyString())).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products/brand")
                        .param("brand", "Test Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].category").value("Test Category"))
                .andExpect(jsonPath("$[0].brand").value("Test Brand"))
                .andExpect(jsonPath("$[0].stock_quantity").value(500));

        verify(productService, times(1)).getProductsByBrand(anyString());
    }

    @Test
    @DisplayName("Delete product by id should return ok")
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_ShouldReturnOk() throws Exception {
        doNothing().when(productService).deleteProductById(anyLong());

        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProductById(anyLong());
    }
}
