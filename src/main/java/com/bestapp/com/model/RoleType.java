package com.bestapp.com.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum RoleType implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    private final String roleValue;

    @Override
    public String getAuthority() {
        return "ROLE_" + roleValue;
    }
}
