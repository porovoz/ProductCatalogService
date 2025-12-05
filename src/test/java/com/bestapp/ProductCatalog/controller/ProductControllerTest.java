package com.bestapp.ProductCatalog.controller;

import com.bestapp.com.controller.ProductController;
import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductService productService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("Create product success")
    void createProductSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "user");

        CreateOrUpdateProductDTO dto = new CreateOrUpdateProductDTO();
        dto.setName("Product1");
        dto.setDescription("Desc");
        dto.setPrice(100.0);
        dto.setCategory("Category1");
        dto.setBrand("Brand1");
        dto.setStockQuantity(10);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Product1");

        when(productService.createProduct(any())).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Product1\",\"description\":\"Desc\",\"price\":100.0,\"category\":\"Category1\",\"brand\":\"Brand1\",\"stockQuantity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Product1"));

        verify(productService, times(1)).createProduct(any());
    }

    @Test
    @DisplayName("Create product unauthorized")
    void createProductUnauthorized() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Product1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get all products success")
    void getAllProductsSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "user");

        ProductDTO dto1 = new ProductDTO();
        dto1.setId(1L);
        dto1.setName("Product1");

        when(productService.findAllProducts(1, 10)).thenReturn(List.of(dto1));

        mockMvc.perform(get("/api/products")
                        .session(session)
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product1"));
    }

    @Test
    @DisplayName("Get products by brand success")
    void getProductsByBrandSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "user");

        ProductDTO dto = new ProductDTO();
        dto.setName("Product1");

        when(productService.getProductsByBrand("Brand1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/products/brand")
                        .session(session)
                        .param("brand", "Brand1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product1"));
    }

    @Test
    @DisplayName("Update product success")
    void updateProductSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "user");

        CreateOrUpdateProductDTO dto = new CreateOrUpdateProductDTO();
        dto.setName("Updated");
        dto.setDescription("Desc");
        dto.setPrice(200.0);
        dto.setCategory("Cat");
        dto.setBrand("Brand");
        dto.setStockQuantity(5);

        ProductDTO updated = new ProductDTO();
        updated.setName("Updated");

        when(productService.updateProduct(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"Desc\",\"price\":200.0,\"category\":\"Cat\",\"brand\":\"Brand\",\"stockQuantity\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("Delete product success")
    void deleteProductSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "user");

        doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api/products/1").session(session))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProductById(1L);
    }

    @Test
    @DisplayName("Any endpoint unauthorized")
    void anyEndpointUnauthorized() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

}
