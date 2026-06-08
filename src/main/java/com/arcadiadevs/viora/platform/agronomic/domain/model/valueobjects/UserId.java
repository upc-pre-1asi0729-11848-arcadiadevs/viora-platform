package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Value object that wraps the unique identifier of a User.
 */
public record UserId(Long value) {
    public UserId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("UserId must be a positive value");
    }
}
