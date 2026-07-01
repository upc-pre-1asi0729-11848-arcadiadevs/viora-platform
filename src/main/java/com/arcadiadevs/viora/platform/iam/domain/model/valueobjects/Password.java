package com.arcadiadevs.viora.platform.iam.domain.model.valueobjects;

/**
 * Password value object.
 * Validates the raw password following the rules defined in the system.
 */
public record Password(String password) {
    public Password {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 8 || password.length() > 255) {
            throw new IllegalArgumentException("Password must be between 8 and 255 characters");
        }
    }
}
