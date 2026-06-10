package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Represents the level of climate risk.
 */
public enum ClimateRiskLevel {
    LOW,
    MODERATE,
    HIGH,
    EXTREME,
    UNKNOWN;

    public static ClimateRiskLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClimateRiskLevel is required");
        }
        try {
            return ClimateRiskLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid ClimateRiskLevel '%s'. Allowed: LOW, MODERATE, HIGH, EXTREME, UNKNOWN".formatted(value));
        }
    }
}