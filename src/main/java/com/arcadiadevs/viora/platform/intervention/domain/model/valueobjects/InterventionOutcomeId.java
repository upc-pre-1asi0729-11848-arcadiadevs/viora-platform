package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record InterventionOutcomeId(Long value) {
    public InterventionOutcomeId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Intervention outcome ID must be provided and positive");
        }
    }
}
