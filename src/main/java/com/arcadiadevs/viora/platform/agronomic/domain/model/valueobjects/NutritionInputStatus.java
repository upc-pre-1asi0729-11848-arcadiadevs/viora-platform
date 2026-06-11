package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Represents the application priority of a nutrition input within a plan.
 */
public enum NutritionInputStatus {
    RECOMMENDED,
    OPTIONAL;

    public static NutritionInputStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("NutritionInputStatus is required");
        }
        try {
            return NutritionInputStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid NutritionInputStatus '%s'. Allowed: RECOMMENDED, OPTIONAL".formatted(value));
        }
    }
}
