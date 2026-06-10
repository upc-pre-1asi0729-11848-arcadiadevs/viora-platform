package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a recommendation for nutritional input.
 */
@Getter
@EqualsAndHashCode
public class NutritionInputRecommendation {

    private final String value;

    public NutritionInputRecommendation(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Nutrition input recommendation cannot be null or empty.");
        }
        this.value = value;
    }
}