package com.bestapp.com.service.impl;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.AuthService;
import com.bestapp.com.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Implementation of {@link ProductService} that handles business logic for managing products.
 * <p>This class acts as a service layer between controllers and storage:
 * it delegates CRUD operations to {@link ProductRepository}.</p>
 */
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AuditLogger auditLogger;
    private final AuthService authService;

    /**
     * Saves a new product into the repository.
     *
     * @param product the product to create
     */
    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
        auditLogger.log(authService.getCurrentUser() + " added product: " + product.getName());
    }

    /**
     * Deletes a product.
     *
     * @param id ID of product to delete
     */
    @Override
    public void removeProductById(String id) {
        productRepository.deleteById(id);
        auditLogger.log(authService.getCurrentUser() + " removed product with ID: " + id);
    }

    /**
     * Updates an existing product.
     *
     * @param id      identifier of product to update
     * @param product new product data
     */
    @Override
    public void updateProductById(String id, Product product) {
        productRepository.updateById(id, product);
        auditLogger.log(authService.getCurrentUser() + " updated product with ID: " + id);
    }

    /**
     * Returns all products, using cache if available.
     *
     * @return list of all products in storage
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Returns products filtered by category, with caching.
     *
     * @param category category string
     * @return list of matching products
     */
    @Override
    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Returns products filtered by brand, using cache.
     *
     * @param brand brand name
     * @return list of products with given brand
     */
    @Override
    public List<Product> getByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    /**
     * Returns products within price range.
     *
     * @param min minimum price (inclusive)
     * @param max maximum price (inclusive)
     * @return list of products in price range
     */
    @Override
    public List<Product> getByPriceRange(double min, double max) {
        return productRepository.findByPriceRange(min, max);
    }

    /**
     * Returns true if product exists by ID.
     */
    @Override
    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }

}
