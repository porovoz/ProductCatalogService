package com.bestapp.com.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrUpdateProductDTO {

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
