package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Represents the lifecycle status of a dynamic nutrition plan.
 */
public enum NutritionPlanStatus {
    ACTIVE,
    SUPERSEDED,
    EXPIRED,
    COMPLETED;

    public static NutritionPlanStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("NutritionPlanStatus is required");
        }
        try {
            return NutritionPlanStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid NutritionPlanStatus '%s'. Allowed: ACTIVE, SUPERSEDED, EXPIRED, COMPLETED".formatted(value));
        }
    }
}
