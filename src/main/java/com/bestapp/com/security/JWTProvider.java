package com.bestapp.com.security;

import com.bestapp.com.dto.UserDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JWTProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JWTProvider(@Value("${jwt.secret.access}") String jwtAccessSecret,
                       @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull UserDTO user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstantTime = now.plusMinutes(60).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpirationDate = Date.from(accessExpirationInstantTime);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessExpirationDate)
                .signWith(jwtAccessSecret, SignatureAlgorithm.HS256)
                .claim("role", user.getRole().name())
                .claim("username", user.getUsername())
                .compact();
    }

    public String generateRefreshToken(@NonNull UserDTO user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstantTime = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpirationDate = Date.from(refreshExpirationInstantTime);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(refreshExpirationDate)
                .signWith(jwtRefreshSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported json web token", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed json web token");
        } catch (SignatureException e) {
            log.error("Invalid signature", e);
        } catch (Exception e) {
            log.error("Invalid token", e);
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
