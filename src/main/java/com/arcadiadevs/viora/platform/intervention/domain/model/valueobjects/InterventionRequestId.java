package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Value object representing a unique identifier for an Intervention Request.
 */
public record InterventionRequestId(Long value) {
    public InterventionRequestId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Intervention Request ID must be a positive number.");
        }
    }
}
