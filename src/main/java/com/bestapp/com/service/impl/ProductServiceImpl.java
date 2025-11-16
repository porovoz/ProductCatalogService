package com.bestapp.com.service.impl;

import com.bestapp.com.cache.CacheType;
import com.bestapp.com.cache.ProductCache;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
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
    private final ProductCache cache = new ProductCache();

    /**
     * Saves a new product into the repository.
     *
     * @param product the product to create
     */
    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
        cache.clearAll();
    }

    /**
     * Deletes a product.
     *
     * @param id ID of product to delete
     */
    @Override
    public void removeProductById(Long id) {
        productRepository.deleteById(id);
        cache.clearAll();
    }

    /**
     * Updates an existing product.
     *
     * @param id      identifier of product to update
     * @param product new product data
     */
    @Override
    public void updateProductById(Long id, Product product) {
        productRepository.updateById(id, product);
        cache.clearAll();
    }

    /**
     * Returns all products, using cache if available.
     *
     * @return list of all products in storage
     */
    @Override
    public List<Product> getAllProducts() {
        List<Product> cached = cache.getFromCache("all", CacheType.ALL);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> all = productRepository.findAll();
        cache.addToCache("all", CacheType.ALL, all);
        return all;
    }

    /**
     * Returns products filtered by category, with caching.
     *
     * @param category category string
     * @return list of matching products
     */
    @Override
    public List<Product> getByCategory(String category) {
        List<Product> cached = cache.getFromCache(category, CacheType.CATEGORY);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> result = productRepository.findByCategory(category);
        cache.addToCache(category, CacheType.CATEGORY, result);
        return result;
    }

    /**
     * Returns products filtered by brand, using cache.
     *
     * @param brand brand name
     * @return list of products with given brand
     */
    @Override
    public List<Product> getByBrand(String brand) {
        List<Product> cached = cache.getFromCache(brand, CacheType.BRAND);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> result = productRepository.findByBrand(brand);
        cache.addToCache(brand, CacheType.BRAND, result);
        return result;
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
        String key = min + "-" + max;
        List<Product> cached = cache.getFromCache(key, CacheType.PRICE);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> result = productRepository.findByPriceRange(min, max);
        cache.addToCache(key, CacheType.PRICE, result);
        return result;
    }

    /**
     * Returns true if product exists by ID.
     */
    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    /**
     * @return cache instance.
     */
    public ProductCache getCache() {
        return cache;
    }

}
