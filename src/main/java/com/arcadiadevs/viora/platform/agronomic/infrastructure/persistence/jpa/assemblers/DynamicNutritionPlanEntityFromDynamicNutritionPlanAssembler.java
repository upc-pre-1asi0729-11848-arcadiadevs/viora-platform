package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.DynamicNutritionPlanEntity;

import java.util.Objects;

/**
 * Assembler to convert DynamicNutritionPlan domain aggregate into DynamicNutritionPlanEntity.
 */
public class DynamicNutritionPlanEntityFromDynamicNutritionPlanAssembler {

    private DynamicNutritionPlanEntityFromDynamicNutritionPlanAssembler() {
    }

    public static DynamicNutritionPlanEntity toEntityFromAggregate(
            DynamicNutritionPlan dynamicNutritionPlan
    ) {
        Objects.requireNonNull(dynamicNutritionPlan, "Dynamic nutrition plan aggregate is required.");

        var entity = new DynamicNutritionPlanEntity();

        if (dynamicNutritionPlan.getId() != null) {
            entity.setId(dynamicNutritionPlan.getId().getValue());
        }

        entity.setUserId(dynamicNutritionPlan.getUserId().getValue());
        entity.setPlotId(dynamicNutritionPlan.getPlotId().getValue());
        entity.setStatus(dynamicNutritionPlan.getStatus());
        entity.setInputRecommendations(dynamicNutritionPlan.getInputRecommendations());
        entity.setApplicationWindowStart(dynamicNutritionPlan.getApplicationWindow().getStartDate());
        entity.setApplicationWindowEnd(dynamicNutritionPlan.getApplicationWindow().getEndDate());
        entity.setRationaleSummary(dynamicNutritionPlan.getRationale().getSummary());
        entity.setRationaleRiskLevel(dynamicNutritionPlan.getRationale().getTriggeringRiskLevel().name());
        entity.setRationaleNdviValue(dynamicNutritionPlan.getRationale().getNdviValue().getValue());
        entity.setRationaleTemperatureAnomaly(dynamicNutritionPlan.getRationale().getTemperatureAnomaly());
        entity.setGeneratedDate(dynamicNutritionPlan.getGeneratedDate());

        return entity;
    }
}
