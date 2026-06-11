package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Dynamic nutrition plan repository port.
 *
 * <p>
 * Defines the persistence operations required by the agronomic domain
 * to retrieve and record dynamic nutrition plans.
 * </p>
 */
public interface DynamicNutritionPlanRepository {

    /**
     * Finds a dynamic nutrition plan by its ID.
     *
     * @param id The dynamic nutrition plan ID.
     * @return The dynamic nutrition plan if found.
     */
    Optional<DynamicNutritionPlan> findById(DynamicNutritionPlanId id);

    /**
     * Finds the active dynamic nutrition plan for a specific user and plot.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @return The active dynamic nutrition plan if found.
     */
    Optional<DynamicNutritionPlan> findActiveByUserIdAndPlotId(UserId userId, PlotId plotId);

    /**
     * Saves a dynamic nutrition plan.
     *
     * @param dynamicNutritionPlan The dynamic nutrition plan to save.
     * @return The saved dynamic nutrition plan.
     */
    DynamicNutritionPlan save(DynamicNutritionPlan dynamicNutritionPlan);
}
