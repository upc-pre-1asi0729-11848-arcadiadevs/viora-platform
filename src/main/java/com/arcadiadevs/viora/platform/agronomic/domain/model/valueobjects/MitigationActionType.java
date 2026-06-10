package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Represents the type of mitigation action recommended.
 */
public enum MitigationActionType {
    IRRIGATION,
    PEST_CONTROL,
    DISEASE_CONTROL,
    NUTRIENT_APPLICATION,
    SOIL_TREATMENT,
    OTHER;

    public static MitigationActionType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MitigationActionType is required");
        }
        try {
            return MitigationActionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid MitigationActionType '%s'. Allowed: IRRIGATION, PEST_CONTROL, DISEASE_CONTROL, NUTRIENT_APPLICATION, SOIL_TREATMENT, OTHER".formatted(value));
        }
    }
}