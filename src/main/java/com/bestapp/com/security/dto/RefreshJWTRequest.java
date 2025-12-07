package com.bestapp.com.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJWTRequest {

    private String refreshToken;
}
