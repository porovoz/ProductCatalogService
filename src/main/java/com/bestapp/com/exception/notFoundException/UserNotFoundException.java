package com.bestapp.com.exception.notFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super("User not found");
    }
}
