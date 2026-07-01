package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Represents the area where the treatment was applied.
 * e.g., "South-west block - 0.6 h"
 *
 * @param description a description of the applied area
 */
public record AppliedArea(String description) {
    public AppliedArea {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Applied area description cannot be null or empty");
        }
    }
}
