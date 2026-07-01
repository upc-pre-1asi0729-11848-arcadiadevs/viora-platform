package com.arcadiadevs.viora.platform.iam.domain.model.valueobjects;

/**
 * Username value object.
 * Validates the username following the rules defined in the system.
 */
public record Username(String username) {
    public Username {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
    }
}
