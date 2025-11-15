package com.bestapp.com.cache;

import com.bestapp.com.model.Product;

import java.util.*;

/**
 * Provides caching for frequently requested product queries.
 * <p>
 * Caches search results by category, brand, and price range.
 * Also tracks cache hit/miss statistics.
 * </p>
 */
public class ProductCache {

    private final Map<CacheType, Map<String, List<Product>>> cache = new EnumMap<>(CacheType.class);
    private int cacheHits = 0;
    private int cacheMisses = 0;

    /**
     * Initializes cache maps for all {@link CacheType} values.
     */
    public ProductCache() {
        for (CacheType type : CacheType.values()) {
            cache.put(type, new HashMap<>());
        }
    }

    /**
     * Retrieves a cached list of products by key and cache type.
     *
     * @param key  cache lookup key (e.g., category name, brand, price range).
     * @param type cache category.
     * @return cached list or empty list if not found.
     */
    public List<Product> getFromCache(String key, CacheType type) {
        List<Product> products = cache.get(type).get(key);
        if (products != null) {
            cacheHits++;
            System.out.println("Cache hit: " + type + " -> " + key);
            return Collections.unmodifiableList(products);
        }
        cacheMisses++;
        return List.of();
    }

    /**
     * Adds a product list to cache.
     * A defensive copy is stored to avoid accidental external modification.
     *
     * @param key      cache key (e.g., category name).
     * @param type     cache type.
     * @param products list of products to store.
     */
    public void addToCache(String key, CacheType type, List<Product> products) {
        cache.get(type).put(key, new ArrayList<>(products));
    }

    /**
     * Clears all cached data and resets statistics.
     */
    public void clearAll() {
        cache.values().forEach(Map::clear);
        cacheHits = 0;
        cacheMisses = 0;
    }

    /**
     * Returns count of cache hits.
     *
     * @return hits
     */
    public int getCacheHits() {
        return cacheHits;
    }

    /**
     * Returns count of cache misses.
     *
     * @return misses
     */
    public int getCacheMisses() {
        return cacheMisses;
    }

}
