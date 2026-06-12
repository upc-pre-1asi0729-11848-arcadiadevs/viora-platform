package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldEstimationPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldForecast;
import org.springframework.stereotype.Service;

/**
 * Domain service that produces a transparent, explainable yield estimate.
 *
 * <p>
 * The estimate combines vegetation vigor (NDVI scaled between a floor and an
 * optimal value), chill adequacy (accumulated chill portions versus the crop's
 * requirement, applied as a bounded modifier) and the plot area:
 * {@code yield ≈ basePotential × vigorFactor × chillModifier × areaHectares}.
 * </p>
 *
 * <p>
 * This deliberately replaces the previous demonstrative formula with a
 * principled heuristic, but it is still an estimate driven by
 * {@link YieldEstimationPolicy} course-level parameters — not a calibrated
 * prediction validated against field yield data.
 * </p>
 */
@Service
public class YieldForecastEstimator {

    private final YieldEstimationPolicy policy;

    public YieldForecastEstimator(YieldEstimationPolicy policy) {
        this.policy = policy;
    }

    /**
     * Estimates total yield in tonnes for a plot.
     *
     * @param ndvi Current NDVI (vegetation vigor).
     * @param chillPortions Accumulated chill portions; treated as zero adequacy when 0.
     * @param areaHectares Plot area in hectares.
     * @return The estimated yield forecast in tonnes (never negative).
     */
    public YieldForecast estimate(double ndvi, double chillPortions, double areaHectares) {
        double vigorFactor = clampUnitInterval(
                (ndvi - policy.ndviFloor()) / (policy.ndviOptimal() - policy.ndviFloor()));
        double chillAdequacy = clampUnitInterval(chillPortions / policy.chillRequirementPortions());
        double chillModifier = policy.chillMinFactor() + (1.0 - policy.chillMinFactor()) * chillAdequacy;

        double tonnes = policy.baseYieldTonnesPerHectare()
                * vigorFactor
                * chillModifier
                * Math.max(0.0, areaHectares);

        return new YieldForecast(roundToOneDecimal(Math.max(0.0, tonnes)));
    }

    private double clampUnitInterval(double value) {
        if (Double.isNaN(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
