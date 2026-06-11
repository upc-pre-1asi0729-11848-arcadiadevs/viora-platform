package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

/**
 * Resource for NutritionInputRecommendation.
 */
public record NutritionInputRecommendationResource(
        String value,
        String purpose,
        Double dosage,
        String dosageUnit,
        String status
) {
}
