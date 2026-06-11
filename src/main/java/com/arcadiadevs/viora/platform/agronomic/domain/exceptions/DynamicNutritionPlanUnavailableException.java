package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when a dynamic nutrition plan cannot be generated,
 * either because there is no significant climate risk or because the
 * agronomic data required to assemble the plan is missing.
 */
public class DynamicNutritionPlanUnavailableException extends RuntimeException {

    /**
     * Creates the exception with the reason the plan is unavailable.
     *
     * @param reason The reason the plan cannot be generated.
     */
    public DynamicNutritionPlanUnavailableException(String reason) {
        super(reason);
    }
}
