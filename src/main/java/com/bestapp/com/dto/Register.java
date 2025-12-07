package com.bestapp.com.dto;

import com.bestapp.com.model.RoleType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Register {

    @Size(min = 2, max = 32)
    private String username;

    @Size(min = 8, max = 16)
    private String passwordHash;

    @Size(min = 4, max = 16)
    private RoleType role;
}