package com.bestapp.com.security;

import com.bestapp.com.model.RoleType;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    public JWTAuthentication generate(Claims claims) {
        final JWTAuthentication tokenInfo = new JWTAuthentication();
        tokenInfo.setRole(getRole(claims));
        tokenInfo.setUsername(claims.getSubject());
        return tokenInfo;
    }

    private RoleType getRole(Claims claims) {
        final String role = claims.get("role", String.class);
        return RoleType.valueOf(role);
    }
}
