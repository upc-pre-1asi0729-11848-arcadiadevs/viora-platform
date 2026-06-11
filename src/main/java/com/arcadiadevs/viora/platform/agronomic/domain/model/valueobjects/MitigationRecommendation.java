package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a recommendation for mitigation actions.
 */
@Getter
@EqualsAndHashCode
public class MitigationRecommendation {

    private final MitigationActionType actionType;
    private final NutritionInputRecommendation nutritionInputRecommendation;
    private final TimeWindow applicationWindow;

    public MitigationRecommendation(
            MitigationActionType actionType,
            NutritionInputRecommendation nutritionInputRecommendation,
            TimeWindow applicationWindow,
            ClimateRiskLevel currentRiskLevel // Used for validation, not stored as state
    ) {
        validateRequiredFields(actionType, nutritionInputRecommendation, applicationWindow, currentRiskLevel);
        validateDomainRestrictions(actionType, currentRiskLevel);

        this.actionType = actionType;
        this.nutritionInputRecommendation = nutritionInputRecommendation;
        this.applicationWindow = applicationWindow;
    }

    private void validateRequiredFields(
            MitigationActionType actionType,
            NutritionInputRecommendation nutritionInputRecommendation,
            TimeWindow applicationWindow,
            ClimateRiskLevel currentRiskLevel
    ) {
        if (actionType == null) {
            throw new IllegalArgumentException("Mitigation action type is required.");
        }
        if (nutritionInputRecommendation == null) {
            throw new IllegalArgumentException("Nutrition input recommendation is required.");
        }
        if (applicationWindow == null) {
            throw new IllegalArgumentException("Application window is required.");
        }
        if (currentRiskLevel == null) {
            throw new IllegalArgumentException("Current risk level is required for validation.");
        }
    }

    private void validateDomainRestrictions(
            MitigationActionType actionType,
            ClimateRiskLevel currentRiskLevel
    ) {
        // Example business rule: For HIGH or EXTREME risk levels, a specific mitigation action is required.
        if ((currentRiskLevel == ClimateRiskLevel.HIGH || currentRiskLevel == ClimateRiskLevel.EXTREME)
                && actionType == MitigationActionType.OTHER) {
            throw new IllegalArgumentException("For HIGH or EXTREME risk levels, a specific mitigation action type must be provided.");
        }
        // Add more domain restrictions as needed.
    }
}