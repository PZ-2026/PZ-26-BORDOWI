package com.dentflow.identity.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Rejestracja konta OWNER w identity-service.
 * Dane gabinetu (nazwa, lokalizacja) są osobno wysyłane do core-service
 * endpointem POST /tenants/register.
 */
public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password
) {}
