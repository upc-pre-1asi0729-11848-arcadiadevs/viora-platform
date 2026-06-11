package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

/**
 * RecommendDynamicNutrition command.
 *
 * <p>
 * Represents the intention of generating a dynamic nutrition plan
 * for a plot owned by a user, based on its current agronomic condition.
 * </p>
 *
 * @param userId The owner user identifier.
 * @param plotId The plot identifier the plan will be generated for.
 */
public record RecommendDynamicNutritionCommand(
        Long userId,
        Long plotId
) {

    public RecommendDynamicNutritionCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }

        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
    }
}
