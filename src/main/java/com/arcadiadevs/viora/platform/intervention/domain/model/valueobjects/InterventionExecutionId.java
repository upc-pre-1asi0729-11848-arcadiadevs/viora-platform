package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Value object representing the unique identifier of an intervention execution.
 *
 * @param value the identifier value
 */
public record InterventionExecutionId(Long value) {
    public InterventionExecutionId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Intervention execution ID must be provided and positive");
        }
    }
}
