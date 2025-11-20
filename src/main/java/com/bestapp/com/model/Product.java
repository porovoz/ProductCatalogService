package com.bestapp.com.model;

import lombok.*;

/**
 * Represents a product in the marketplace.
 * Contains basic product information and unique identifier.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String brand;
    private int stockQuantity;

    public Product(String name, String description, double price, String category, String brand, int stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand;
        this.stockQuantity = stockQuantity;
    }

}