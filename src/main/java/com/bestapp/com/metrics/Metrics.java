package com.bestapp.com.metrics;

import com.bestapp.com.service.ProductService;

/**
 * Provides simple metrics: request duration and product count.
 */
public class Metrics {

    private long startTime;

    /**
     * Starts timing a performance measurement.
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Returns elapsed time since {@link #start()} was called.
     *
     * @return elapsed time in milliseconds.
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the total number of products.
     *
     * @param productService ProductService instance.
     * @return total product count.
     */
    public int getProductCount(ProductService productService) {
        return productService.getAllProducts().size();
    }

}