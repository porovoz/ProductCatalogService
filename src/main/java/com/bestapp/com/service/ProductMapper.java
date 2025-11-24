package com.bestapp.com.service;

import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * {@code ProductMapper} is an interface used for mapping between the {@link Product} model
 * and the {@link ProductDTO} Data Transfer Object (DTO).
 * It leverages the MapStruct library for automatic code generation of the mapping implementation.
 */
@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    /**
     * Converts a {@link Product} entity to a {@link ProductDTO}.
     *
     * @param product the {@link Product} entity to be converted
     * @return the corresponding {@link ProductDTO}
     */
    ProductDTO productToProductDTO(Product product);

    /**
     * Converts a {@link ProductDTO} to a {@link Product} entity.
     *
     * @param productDTO the {@link ProductDTO} to be converted
     * @return the corresponding {@link Product} entity
     */
    Product productDTOToProduct(ProductDTO productDTO);

}
