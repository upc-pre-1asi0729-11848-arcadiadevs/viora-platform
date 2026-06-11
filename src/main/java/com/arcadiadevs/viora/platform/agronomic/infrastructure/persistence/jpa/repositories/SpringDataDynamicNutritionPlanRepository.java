package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionPlanStatus;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.DynamicNutritionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for DynamicNutritionPlan entities.
 */
public interface SpringDataDynamicNutritionPlanRepository extends JpaRepository<DynamicNutritionPlanEntity, Long> {

    /**
     * Finds the most recent dynamic nutrition plan entity for a user, plot and status.
     *
     * @param userId The ID of the user.
     * @param plotId The ID of the plot.
     * @param status The plan status.
     * @return The most recently generated plan entity if found.
     */
    Optional<DynamicNutritionPlanEntity> findFirstByUserIdAndPlotIdAndStatusOrderByGeneratedDateDesc(
            Long userId,
            Long plotId,
            NutritionPlanStatus status
    );
}
