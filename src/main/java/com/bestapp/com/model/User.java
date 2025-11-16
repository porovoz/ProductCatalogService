package com.bestapp.com.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a user.
 * Contains basic user information and unique identifier.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String role;
}
