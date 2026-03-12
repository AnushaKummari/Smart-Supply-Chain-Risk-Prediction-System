package com.ssrp.dto;

public record LoginRequest(
        String email,
        String password
) {}

