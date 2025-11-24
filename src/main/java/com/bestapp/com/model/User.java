package com.bestapp.com.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
