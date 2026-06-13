package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Domain service for evaluating and classifying climate risk levels.
 *
 * <p>
 * This service encapsulates the pure business logic for determining the
 * ClimateRiskLevel based on various agronomic aggregates and statistics
 * related to olive cultivation.
 * </p>
 */
@Service
public class ClimateRiskEvaluator {

    /**
     * Evaluates current agronomic climate risk using provider-backed weather and
     * the latest vegetation signal.
     *
     * <p>
     * Weather remains authoritative for immediate extreme, high and moderate
     * conditions. NDVI can raise the risk when vegetation vigor is below the
     * configured thresholds. Seasonal chill accumulation is deliberately not
     * used here: a low accumulated value is not a current climate hazard without
     * also considering season progress and the plot-specific requirement.
     * </p>
     *
     * @param ndviValue Latest satellite NDVI.
     * @param weatherSnapshot Current provider-backed weather.
     * @param policy Configured thresholds shared by monitoring and nutrition.
     * @return The consolidated risk level.
     */
    public ClimateRiskLevel evaluateClimateRisk(
            NdviValue ndviValue,
            WeatherSnapshot weatherSnapshot,
            DynamicNutritionPolicy policy
    ) {
        Objects.requireNonNull(ndviValue, "NDVI value is required.");
        Objects.requireNonNull(weatherSnapshot, "Weather snapshot is required.");
        Objects.requireNonNull(policy, "Agronomic risk policy is required.");

        var weatherRisk = weatherSnapshot.getClimateRiskLevel();
        if (weatherRisk == ClimateRiskLevel.EXTREME
                || weatherRisk == ClimateRiskLevel.HIGH) {
            return weatherRisk;
        }

        if (ndviValue.getValue() < policy.highRiskNdviThreshold()) {
            return ClimateRiskLevel.HIGH;
        }

        if (ndviValue.getValue() < policy.moderateRiskNdviThreshold()
                || weatherRisk == ClimateRiskLevel.MODERATE) {
            return ClimateRiskLevel.MODERATE;
        }

        return weatherRisk == ClimateRiskLevel.UNKNOWN
                ? ClimateRiskLevel.UNKNOWN
                : ClimateRiskLevel.LOW;
    }
}
