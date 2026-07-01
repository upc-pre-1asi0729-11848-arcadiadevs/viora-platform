package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Value object representing the grace period status.
 * e.g., "14 days completed"
 *
 * @param description the description of the grace period status
 */
public record GracePeriod(String description) {
    public GracePeriod {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Grace period description cannot be null or empty");
        }
    }
}
