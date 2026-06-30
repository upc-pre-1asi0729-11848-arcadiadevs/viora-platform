package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record SprayVolume(Integer amount, String unit) {
    public SprayVolume {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Spray volume amount must be non-negative");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Spray volume unit cannot be blank");
        }
    }
}
