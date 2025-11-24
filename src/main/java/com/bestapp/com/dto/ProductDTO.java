package com.bestapp.com.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * {@code ProductDTO} is a Data Transfer Object (DTO) that represents a product in the system.
 * It encapsulates the details of a product including its ID, name, description, price, category,
 * brand, and stock quantity. This class is used for transferring product data between
 * different layers of the application.
 */
@Data
public class ProductDTO {

    private Long id;

    @NotNull(message = "Name cannot be null.")
    @Size(min = 1, max = 100, message = "Name must be 1–100 characters long.")
    private String name;

    @NotNull(message = "Description cannot be null.")
    private String description;

    @Positive(message = "Price must be positive.")
    private double price;

    @NotNull(message = "Category is required.")
    private String category;

    @NotNull(message = "Brand is required.")
    private String brand;

    @PositiveOrZero(message = "Quantity cannot be negative.")
    private int stockQuantity;

}
