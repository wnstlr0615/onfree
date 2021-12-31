package com.onfree.config.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtLoginResponse {
    private final boolean result;
    private final String accessToken;
    private final String refreshToken;
    private final String username;
}
