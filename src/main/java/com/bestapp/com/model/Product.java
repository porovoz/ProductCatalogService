package com.bestapp.com.model;

import lombok.*;

import java.util.UUID;

/**
 * Represents a product in the marketplace.
 * Contains basic product information and unique identifier.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {

    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String brand;
    private int stockQuantity;

    public Product(String name, String description, double price, String category, String brand, int stockQuantity) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand;
        this.stockQuantity = stockQuantity;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be null or blank");
        }
        this.id = id;
    }

}