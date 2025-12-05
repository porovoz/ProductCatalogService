package com.bestapp.com.exception;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException() {
        super("Product not found!");
    }
}
