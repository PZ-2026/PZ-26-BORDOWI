package com.dentflow.identity.auth.api;

public record AuthResponse(
        String token,
        Long userId,
        String email,
        Long tenantId
) {}
