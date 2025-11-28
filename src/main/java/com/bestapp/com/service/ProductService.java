package com.bestapp.com.service;

import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;

import java.util.List;

/**
 * Defines product-related operations for managing products.
 */
public interface ProductService {

    /**
     * Adds a new product.
     */
    ProductDTO createProduct(CreateOrUpdateProductDTO createOrUpdateProductDTO);

    /**
     * Updates an existing product by ID.
     */
    ProductDTO updateProduct(Long id, CreateOrUpdateProductDTO createOrUpdateProductDTO);

    /**
     * Removes a product by ID.
     */
    void deleteProductById(Long id);

    /**
     * Returns all products.
     */
    List<ProductDTO> findAllProducts(Integer pageNumber, Integer pageSize);

    /**
     * Returns products filtered by category.
     */
    List<ProductDTO> getProductsByCategory(String category);

    /**
     * Returns products filtered by brand.
     */
    List<ProductDTO> getProductsByBrand(String brand);

    /**
     * Returns products within a price range.
     */
    List<ProductDTO> getProductsByPriceRange(double min, double max);

    /**
     * Checks whether a product with a given ID exists.
     */
    boolean existsById(Long id);

}
