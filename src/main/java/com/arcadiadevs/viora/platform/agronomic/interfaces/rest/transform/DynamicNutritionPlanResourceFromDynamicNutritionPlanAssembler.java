package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.DynamicNutritionPlanResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.NutritionApplicationWindowResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.NutritionInputRecommendationResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlanRationaleResource;

import java.util.Objects;

/**
 * Assembler to convert DynamicNutritionPlan aggregate into DynamicNutritionPlanResource.
 */
public final class DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler {

    private DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler() {
    }

    public static DynamicNutritionPlanResource toResourceFromAggregate(
            DynamicNutritionPlan dynamicNutritionPlan
    ) {
        Objects.requireNonNull(dynamicNutritionPlan, "Dynamic nutrition plan aggregate is required.");

        var inputRecommendationResources = dynamicNutritionPlan.getInputRecommendations().stream()
                .map(DynamicNutritionPlanResourceFromDynamicNutritionPlanAssembler::toResourceFromInputRecommendation)
                .toList();

        var applicationWindowResource = new NutritionApplicationWindowResource(
                dynamicNutritionPlan.getApplicationWindow().getStartDate(),
                dynamicNutritionPlan.getApplicationWindow().getEndDate()
        );

        var rationaleResource = new PlanRationaleResource(
                dynamicNutritionPlan.getRationale().getSummary(),
                dynamicNutritionPlan.getRationale().getTriggeringRiskLevel().name(),
                dynamicNutritionPlan.getRationale().getNdviValue().getValue(),
                dynamicNutritionPlan.getRationale().getTemperatureAnomaly()
        );

        return new DynamicNutritionPlanResource(
                dynamicNutritionPlan.getId() != null ? dynamicNutritionPlan.getId().getValue() : null,
                dynamicNutritionPlan.getUserId().getValue(),
                dynamicNutritionPlan.getPlotId().getValue(),
                dynamicNutritionPlan.getStatus().name(),
                inputRecommendationResources,
                applicationWindowResource,
                rationaleResource,
                dynamicNutritionPlan.getGeneratedDate()
        );
    }

    private static NutritionInputRecommendationResource toResourceFromInputRecommendation(
            NutritionInputRecommendation inputRecommendation
    ) {
        return new NutritionInputRecommendationResource(
                inputRecommendation.getValue(),
                inputRecommendation.getPurpose(),
                inputRecommendation.getDosage(),
                inputRecommendation.getDosageUnit(),
                inputRecommendation.getStatus().name()
        );
    }
}
