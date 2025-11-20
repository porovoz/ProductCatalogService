package com.bestapp.com.service;

import com.bestapp.com.cache.ProductCache;
import com.bestapp.com.model.Product;

import java.util.List;

/**
 * Defines product-related operations for managing products.
 */
public interface ProductService {

    /**
     * Adds a new product.
     */
    void addProduct(Product product);

    /**
     * Updates an existing product by ID.
     */
    void updateProductById(Long id, Product product);

    /**
     * Removes a product by ID.
     */
    void removeProductById(Long id);

    /**
     * Returns all products.
     */
    List<Product> getAllProducts();

    /**
     * Returns products filtered by category.
     */
    List<Product> getByCategory(String category);

    /**
     * Returns products filtered by brand.
     */
    List<Product> getByBrand(String brand);

    /**
     * Returns products within a price range.
     */
    List<Product> getByPriceRange(double min, double max);

    /**
     * Checks whether a product with a given ID exists.
     */
    boolean existsById(Long id);

    ProductCache getCache();

}
