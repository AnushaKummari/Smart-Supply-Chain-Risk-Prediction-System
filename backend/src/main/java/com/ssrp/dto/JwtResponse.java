package com.ssrp.dto;

public record JwtResponse(
        String accessToken,
        String tokenType
) {
    public JwtResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}

