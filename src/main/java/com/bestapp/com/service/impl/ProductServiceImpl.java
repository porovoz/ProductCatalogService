package com.bestapp.com.service.impl;

import com.bestapp.com.cache.CacheType;
import com.bestapp.com.cache.ProductCache;
import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.exception.notFoundException.ProductNotFoundException;
import com.bestapp.com.model.Product;
import com.bestapp.com.repository.ProductRepository;
import com.bestapp.com.service.ProductMapper;
import com.bestapp.com.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link ProductService} that handles business logic for managing products.
 * <p>This class acts as a service layer between controllers and storage:
 * it delegates CRUD operations to {@link ProductRepository}.</p>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductCache cache = new ProductCache();

    /**
     * Saves a new product into the repository.
     *
     * @param createOrUpdateProductDTO the product to create
     */
    @Override
    @Transactional
    public ProductDTO createProduct(CreateOrUpdateProductDTO createOrUpdateProductDTO) {
        Product createdProduct = productRepository.save(productMapper.createOrUpdateProductDTOtoProduct(createOrUpdateProductDTO));
        ProductDTO productDTO = productMapper.productToProductDTO(createdProduct);
        cache.clearAll();
        return productDTO;
    }

    /**
     * Deletes a product.
     *
     * @param id ID of product to delete
     */
    @Override
    @Transactional
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
        cache.clearAll();
    }

    /**
     * Updates an existing product.
     *
     * @param id      identifier of product to update
     * @param createOrUpdateProductDTO new product data
     */
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, CreateOrUpdateProductDTO createOrUpdateProductDTO) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        productMapper.updateProduct(createOrUpdateProductDTO, product);
        product = productRepository.save(product);
        ProductDTO productDTO = productMapper.productToProductDTO(product);
        cache.clearAll();
        return productDTO;
    }

    /**
     * Returns all products, using cache if available.
     *
     * @return list of all products in storage
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllProducts(Integer pageNumber, Integer pageSize) {
        if (pageSize > 50 || pageSize <= 0) {
            pageSize = 50;
        }
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Product> cached = cache.getFromCache("all", CacheType.ALL);
        if (!cached.isEmpty()) {
            return productMapper.productListToProductDTOList(cached);
        }
        List<Product> all = productRepository.findAll(pageRequest).getContent();
        cache.addToCache("all", CacheType.ALL, all);
        return productMapper.productListToProductDTOList(all);
    }

    /**
     * Returns products filtered by category, with caching.
     *
     * @param category category string
     * @return list of matching products
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String category) {
        List<Product> cached = cache.getFromCache(category, CacheType.CATEGORY);
        if (!cached.isEmpty()) {
            return productMapper.productListToProductDTOList(cached);
        }
        List<Product> result = productRepository.findByCategoryIgnoreCase(category);
        cache.addToCache(category, CacheType.CATEGORY, result);
        return productMapper.productListToProductDTOList(result);
    }

    /**
     * Returns products filtered by brand, using cache.
     *
     * @param brand brand name
     * @return list of products with given brand
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByBrand(String brand) {
        List<Product> cached = cache.getFromCache(brand, CacheType.BRAND);
        if (!cached.isEmpty()) {
            return productMapper.productListToProductDTOList(cached);
        }
        List<Product> result = productRepository.findByBrandIgnoreCase(brand);
        cache.addToCache(brand, CacheType.BRAND, result);
        return productMapper.productListToProductDTOList(result);
    }

    /**
     * Returns products within price range.
     *
     * @param min minimum price (inclusive)
     * @param max maximum price (inclusive)
     * @return list of products in price range
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(double min, double max) {
        String key = min + "-" + max;
        List<Product> cached = cache.getFromCache(key, CacheType.PRICE);
        if (!cached.isEmpty()) {
            return productMapper.productListToProductDTOList(cached);
        }
        List<Product> result = productRepository.findByPriceBetween(min, max);
        cache.addToCache(key, CacheType.PRICE, result);
        return productMapper.productListToProductDTOList(result);
    }

    /**
     * Returns true if product exists by ID.
     */
    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

}
