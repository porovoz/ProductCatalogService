package com.bestapp.com.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JWTResponse {

    private static final String TOKEN_TYPE = "Bearer";

    private String accessToken;
    private String refreshToken;
}
