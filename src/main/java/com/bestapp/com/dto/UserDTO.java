package com.bestapp.com.dto;

import com.bestapp.com.model.RoleType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * {@code UserDTO} is a Data Transfer Object (DTO) that represents a user in the system.
 * It encapsulates basic information about the user, such as the username and role.
 * This class is used for transferring user-related data between different layers
 * of the application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank(message = "Username can not be blank.")
    private String username;

    @NotBlank(message = "Password can not be blank.")
    private String passwordHash;

    @NotBlank(message = "Role can not be blank.")
    private RoleType role;

}
