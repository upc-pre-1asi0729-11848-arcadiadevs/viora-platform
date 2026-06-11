package com.arcadiadevs.viora.platform.agronomic.domain.exceptions;

/**
 * Exception thrown when no active dynamic nutrition plan exists for a plot.
 */
public class DynamicNutritionPlanNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the plot without an active plan.
     *
     * @param plotId The plot identifier.
     */
    public DynamicNutritionPlanNotFoundException(Long plotId) {
        super("No active dynamic nutrition plan was found for plot with ID %s.".formatted(plotId));
    }
}
