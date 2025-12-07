package com.bestapp.com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * {@code ProductDTO} is a Data Transfer Object (DTO) that represents a product in the system.
 * It encapsulates the details of a product including its ID, name, description, price, category,
 * brand, and stock quantity. This class is used for transferring product data between
 * different layers of the application.
 */
@Data
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String brand;
    private int stockQuantity;

}
