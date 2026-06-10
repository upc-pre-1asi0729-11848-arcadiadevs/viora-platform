package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;

/**
 * Domain service for evaluating and classifying climate risk levels.
 *
 * <p>
 * This service encapsulates the pure business logic for determining the
 * ClimateRiskLevel based on various agronomic aggregates and statistics
 * related to olive cultivation.
 * </p>
 */
public class ClimateRiskEvaluator {

    /**
     * Evaluates the climate risk level based on provided agronomic data.
     *
     * @param ndviValue The NDVI value for the area.
     * @param accumulatedChillHours The accumulated chill hours.
     * @param weatherSnapshot The current weather snapshot.
     * @return The calculated ClimateRiskLevel.
     */
    public ClimateRiskLevel evaluateClimateRisk(
            NdviValue ndviValue,
            AccumulatedChillHours accumulatedChillHours,
            WeatherSnapshot weatherSnapshot
    ) {
        // TODO: Implement the actual business logic for climate risk evaluation.
        // This logic will depend on specific rules and thresholds for olive cultivation.
        // For demonstration, a placeholder logic is provided.

        if (weatherSnapshot.getClimateRiskLevel() == ClimateRiskLevel.EXTREME ||
            weatherSnapshot.getClimateRiskLevel() == ClimateRiskLevel.HIGH) {
            return weatherSnapshot.getClimateRiskLevel();
        }

        if (ndviValue.getValue() < 0.2 && accumulatedChillHours.getValue() < 100) {
            return ClimateRiskLevel.HIGH;
        }

        if (ndviValue.getValue() < 0.4 || accumulatedChillHours.getValue() < 200) {
            return ClimateRiskLevel.MODERATE;
        }

        return ClimateRiskLevel.LOW;
    }
}