package com.bestapp.com.repository;

import com.bestapp.com.cache.CacheType;
import com.bestapp.com.cache.ProductCache;
import com.bestapp.com.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the product repository.
 * <p>
 * Provides CRUD operations, caching of frequent queries, and search functionality.
 * </p>
 */
public class ProductRepository {

    private final Map<String, Product> products = new LinkedHashMap<>();
    private final ProductCache cache = new ProductCache();

    /**
     * Saves a new product to the repository.
     * <p>
     * Clears all cached data after modification.
     * </p>
     *
     * @param product new product to add
     */
    public void save(Product product) {
        products.put(product.getId(), product);
        cache.clearAll();
    }

    /**
     * Updates an existing product by ID using {@link Map#computeIfPresent(Object, java.util.function.BiFunction)}.
     * New product receives old id and the cache is invalidated for related keys.
     * <p>
     * If the ID is not found, an exception is thrown.
     * </p>
     *
     * @param id         product ID to update
     * @param newProduct new product data
     * @throws IllegalArgumentException if the product does not exist
     */
    public void updateById(String id, Product newProduct) {
        boolean updated = products.computeIfPresent(id, (key, old) -> {
            newProduct.setId(id);
            cache.clearAll();
            return newProduct;
        }) != null;
        if (!updated) {
            throw new IllegalArgumentException("Product with ID " + id + " not found.");
        }
    }

    /**
     * Deletes a product from the repository by its ID.
     *
     * @param id ID of the product to remove
     * @throws IllegalArgumentException if the product is not found
     */
    public void deleteById(String id) {
        Product removed = products.remove(id);
        if (removed != null) {
            cache.clearAll();
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " not found.");
        }
    }

    /**
     * Returns all products in the repository.
     * <p>
     * Uses caching for faster access on repeated calls.
     * </p>
     *
     * @return list of all products
     */
    public List<Product> findAll() {
        List<Product> cached = cache.getFromCache("all", CacheType.ALL);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> all = new ArrayList<>(products.values());
        cache.addToCache("all", CacheType.ALL, all);
        return all;
    }

    /**
     * @return uncached list of products (used for persistence).
     * */
    public List<Product> findAllUncached() {
        return new ArrayList<>(products.values());
    }

    /**
     * Searches products by category.
     *
     * @param category category name
     * @return list of matching products or empty list
     */
    public List<Product> findByCategory(String category) {
        List<Product> cached = cache.getFromCache(category, CacheType.CATEGORY);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> result = products.values().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category))
                .toList();
        if (result.isEmpty()) {
            System.out.println("No products found in category: " + category);
        }
        cache.addToCache(category, CacheType.CATEGORY, result);
        return result;
    }

    /**
     * Searches products by brand.
     *
     * @param brand brand name
     * @return list of matching products or empty list
     */
    public List<Product> findByBrand(String brand) {
        List<Product> cached = cache.getFromCache(brand, CacheType.BRAND);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> result = products.values().stream()
                .filter(p -> p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand))
                .toList();
        if (result.isEmpty()) {
            System.out.println("No products found for brand: " + brand);
        }
        cache.addToCache(brand, CacheType.BRAND, result);
        return result;
    }

    /**
     * Searches products within a price range.
     *
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of products within range or empty list
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        String key = minPrice + "-" + maxPrice;
        List<Product> cached = cache.getFromCache(key, CacheType.PRICE);
        if (!cached.isEmpty()) {
            return cached;
        }
        List<Product> result = products.values().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .toList();
        if (result.isEmpty()) {
            System.out.println("No products found in price range: " + minPrice + " - " + maxPrice);
        }
        cache.addToCache(key, CacheType.PRICE, result);
        return result;
    }

    /**
     * @return cache instance.
     */
    public ProductCache getCache() {
        return cache;
    }

    /**
     * @return true if a product exists by ID.
     */
    public boolean existsById(String id) {
        return products.containsKey(id);
    }
}
