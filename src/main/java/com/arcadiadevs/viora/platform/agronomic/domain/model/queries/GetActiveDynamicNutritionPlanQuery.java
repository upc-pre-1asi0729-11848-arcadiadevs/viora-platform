package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * GetActiveDynamicNutritionPlan query.
 *
 * <p>
 * Represents the intention of retrieving the currently active
 * dynamic nutrition plan for a plot owned by a user.
 * </p>
 *
 * @param userId The owner user identifier.
 * @param plotId The plot identifier.
 */
public record GetActiveDynamicNutritionPlanQuery(
        Long userId,
        Long plotId
) {

    public GetActiveDynamicNutritionPlanQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }

        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
    }
}
