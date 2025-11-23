package com.bestapp.com.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "Username can not be blank.")
    private String username;

    @NotBlank(message = "Role can not be blank.")
    private String role;
//    private Role role;

}
