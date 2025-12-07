package com.bestapp.com.controller;

import com.bestapp.com.dto.CreateOrUpdateProductDTO;
import com.bestapp.com.dto.ProductDTO;
import com.bestapp.com.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the products
 * @see ProductDTO
 * @see ProductService
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API for products management")
public class ProductController {

    private final ProductService productService;

    /**
     * Creating a new product.
     * @return the response with the created product in JSON format and the HTTP 201 status code (Created).<br>
     */
    @Operation(
            summary = "Create new product",
            description = "Create new product with product id",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Product was successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDTO.class)
                            )),
                    @ApiResponse(responseCode = "201", description = "Product was successfully created", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(Authentication authentication,
                                                    @RequestBody @Valid CreateOrUpdateProductDTO createOrUpdateProductDTO) {
        ProductDTO createdProductDTO = productService.createProduct(createOrUpdateProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDTO);
    }

    /**
     * Getting all products pageable.
     * @param pageNumber page number
     * @param pageSize page size number
     * @return the response with the found product list in JSON format and the HTTP 200 status code (Ok).<br>
     * If the product list not found the HTTP status code 404 (Not found).
     */
    @Operation(
            summary = "Find all products pageable",
            description = "Search all products pageable",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "All products successfully found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDTO.class)
                            )),
                    @ApiResponse(responseCode = "200", description = "All products successfully found", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Products not found", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getAllProducts(Authentication authentication,
                                                           @RequestParam("pageNumber") @Positive Integer pageNumber,
                                                     @RequestParam("pageSize") @Positive Integer pageSize) {
        List<ProductDTO> foundProductDTOS = productService.findAllProducts(pageNumber, pageSize);
        if (foundProductDTOS == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundProductDTOS);
    }

    /**
     * Getting product by brand.
     * @param brand product brand.
     * @return the response with the found product in JSON format and the HTTP 200 status code (Ok).<br>
     * If the product not found the HTTP status code 404 (Not found).
     */
    @Operation(
            summary = "Find product by brand",
            description = "Search by product brand",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Product was successfully found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDTO.class)
                            )),
                    @ApiResponse(responseCode = "200", description = "Product was successfully found", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @GetMapping("/brand")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getProductsByBrand(Authentication authentication, @RequestParam("brand") @NotBlank String brand) {
        List<ProductDTO> foundProductDTOS = productService.getProductsByBrand(brand);
        if (foundProductDTOS == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundProductDTOS);
    }

    /**
     * Getting product by category.
     * @param category product category.
     * @return the response with the found product in JSON format and the HTTP 200 status code (Ok).<br>
     * If the product not found the HTTP status code 404 (Not found).
     */
    @Operation(
            summary = "Find product by category",
            description = "Search by product category",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Product was successfully found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDTO.class)
                            )),
                    @ApiResponse(responseCode = "200", description = "Product was successfully found", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(Authentication authentication,
                                                                  @RequestParam("category") @NotBlank String category) {
        List<ProductDTO> foundProductDTOS = productService.getProductsByCategory(category);
        if (foundProductDTOS == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundProductDTOS);
    }


    /**
     * Getting products by price range.
     * @param min minimal product price.
     * @param max maximal product price.
     * @return the response with the found products in JSON format and the HTTP 200 status code (Ok).<br>
     * If the products not found the HTTP status code 404 (Not found).
     */
    @Operation(
            summary = "Find products by price range",
            description = "Search products by price range",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Product(s) was(were) successfully found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDTO.class)
                            )),
                    @ApiResponse(responseCode = "200", description = "Product(s) was(were) successfully found", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Products not found", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @GetMapping("/price-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(Authentication authentication,
                                                                    @RequestParam("min") @Positive Double min,
                                                                    @RequestParam("max") @Positive Double max) {
        List<ProductDTO> foundProductDTOS = productService.getProductsByPriceRange(min, max);
        if (foundProductDTOS == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundProductDTOS);
    }

    /**
     * Updating product.
     * @param id product identification number.
     * @return the response with the updated product in JSON format and the HTTP 200 status code (Ok).<br>
     * If the product list not found the HTTP status code 404 (Not found).
     */
    @Operation(
            summary = "Update product by id",
            description = "Search product by id to update",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Product was successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDTO.class)
                            )),
                    @ApiResponse(responseCode = "200", description = "Product was successfully updated", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(Authentication authentication,
                                                    @PathVariable("id") @Positive Long id,
                                              @RequestBody @Valid CreateOrUpdateProductDTO createOrUpdateProductDTO) {
        ProductDTO updatedProductDTO = productService.updateProduct(id, createOrUpdateProductDTO);
        if (updatedProductDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProductDTO);
    }

    /**
     * Deleting product by id.
     * @param id product identification number.
     */
    @Operation(
            summary = "Delete product by id",
            description = "Search product by id to delete",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product was successfully deleted", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProductById(Authentication authentication,
                                                  @PathVariable("id") @Positive Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

}

