package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DoseConfirmation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplication;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplicationWindow;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlanRationale;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.DynamicNutritionPlanEntity;

import java.util.List;
import java.util.Objects;

/**
 * Assembler to convert DynamicNutritionPlanEntity into DynamicNutritionPlan domain aggregate.
 */
public class DynamicNutritionPlanFromDynamicNutritionPlanEntityAssembler {

    private DynamicNutritionPlanFromDynamicNutritionPlanEntityAssembler() {
    }

    public static DynamicNutritionPlan toAggregateFromEntity(
            DynamicNutritionPlanEntity entity
    ) {
        Objects.requireNonNull(entity, "Dynamic nutrition plan entity is required.");

        var applicationWindow = new NutritionApplicationWindow(
                entity.getApplicationWindowStart(),
                entity.getApplicationWindowEnd()
        );

        var rationale = new PlanRationale(
                entity.getRationaleSummary(),
                ClimateRiskLevel.fromString(entity.getRationaleRiskLevel()),
                new NdviValue(entity.getRationaleNdviValue()),
                entity.getRationaleTemperatureAnomaly()
        );

        var dynamicNutritionPlan = new DynamicNutritionPlan(
                new UserId(entity.getUserId()),
                new PlotId(entity.getPlotId()),
                entity.getStatus(),
                entity.getInputRecommendations(),
                applicationWindow,
                rationale,
                entity.getGeneratedDate()
        );

        dynamicNutritionPlan.restoreIdentity(new DynamicNutritionPlanId(entity.getId()));

        if (entity.getAppliedDate() != null) {
            var appliedInputs = entity.getAppliedInputs() == null || entity.getAppliedInputs().isBlank()
                    ? List.<String>of()
                    : List.of(entity.getAppliedInputs().split("\n"));
            dynamicNutritionPlan.restoreApplication(new NutritionApplication(
                    entity.getAppliedDate(),
                    entity.getAppliedTime(),
                    appliedInputs,
                    DoseConfirmation.fromString(entity.getDoseConfirmation()),
                    entity.getFieldOperator(),
                    entity.getFieldNotes()
            ));
        }

        return dynamicNutritionPlan;
    }
}
