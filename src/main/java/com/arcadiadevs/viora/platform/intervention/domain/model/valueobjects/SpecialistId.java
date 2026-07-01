package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Value object representing the unique identifier of a Specialist.
 *
 * @param value the numeric ID
 */
public record SpecialistId(Long value) {
    public SpecialistId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Specialist ID must be provided and positive");
        }
    }
}
