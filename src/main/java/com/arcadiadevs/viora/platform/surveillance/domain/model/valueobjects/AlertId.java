package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

/**
 * Alert Identifier.
 */
public record AlertId(Long value) {
    public AlertId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("AlertId must be greater than zero");
        }
    }
}
