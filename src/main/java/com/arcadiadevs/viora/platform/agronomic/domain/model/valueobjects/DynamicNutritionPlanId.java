package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Dynamic nutrition plan identifier value object.
 */
@Getter
@EqualsAndHashCode
public class DynamicNutritionPlanId {

    private final Long value;

    public DynamicNutritionPlanId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Dynamic nutrition plan ID must be a positive number.");
        }
        this.value = value;
    }
}
