package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record Dosage(Double amount, String unit) {
    public Dosage {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Dosage amount must be positive");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Dosage unit cannot be blank");
        }
    }
}
