package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource for DynamicNutritionPlan.
 */
public record DynamicNutritionPlanResource(
        Long dynamicNutritionPlanId,
        Long userId,
        Long plotId,
        String status,
        List<NutritionInputRecommendationResource> inputRecommendations,
        NutritionApplicationWindowResource applicationWindow,
        PlanRationaleResource rationale,
        LocalDate generatedDate
) {
}
