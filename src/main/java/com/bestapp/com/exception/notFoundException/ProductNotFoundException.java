package com.bestapp.com.exception.notFoundException;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException() {
        super("Product not found!");
    }
}
