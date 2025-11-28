package com.bestapp.com.service;

import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
     * Converts a {@link CreateOrUpdateProductDTO} DTO to a {@link Product} entity.
     *
     * @param createOrUpdateProductDTO the {@link CreateOrUpdateProductDTO} DTO to be converted
     * @return the corresponding {@link Product} entity
     */
    Product createOrUpdateProductDTOtoProduct(CreateOrUpdateProductDTO createOrUpdateProductDTO);

    /**
     * Updates a {@link Product} entity from data in {@link CreateOrUpdateProductDTO} DTO.
     *
     * @param createOrUpdateProductDTO the {@link CreateOrUpdateProductDTO} DTO data to update {@link Product} entity
     * @param product the {@link Product} entity to be updated
     */
    @Mapping(target = "id", ignore = true)
    void updateProduct(CreateOrUpdateProductDTO createOrUpdateProductDTO, @MappingTarget Product product);

    /**
     * Converts a {@link ProductDTO} to a {@link Product} entity.
     *
     * @param productDTO the {@link ProductDTO} to be converted
     * @return the corresponding {@link Product} entity
     */
    Product productDTOToProduct(ProductDTO productDTO);

    /**
     * Converts a {@link List<Product>} to a {@link List<ProductDTO>} DTO.
     *
     * @param products the {@link List<Product>} to be converted
     * @return the corresponding {@link List<ProductDTO>} DTO
     */
    List<ProductDTO> productListToProductDTOList(List<Product> products);

}
