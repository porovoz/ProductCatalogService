package com.bestapp.com.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Login {

    @NotNull(message = "Username is required.")
    @Size(min = 2, max = 50, message = "Username must be 2-50 characters long.")
    private String username;

    @NotNull(message = "Password is required.")
    @Size(min = 4, max = 100, message = "Password must be 4–100 characters long.")
    private String password;

}
