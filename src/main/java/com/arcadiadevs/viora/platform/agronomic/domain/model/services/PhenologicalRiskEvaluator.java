package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import org.springframework.stereotype.Service;

/**
 * Domain service that derives a plot's phenological risk from winter-chill
 * fulfilment, optionally compounded by a warm temperature anomaly and a
 * declining vegetation trend.
 *
 * <p>
 * For olives (and other temperate fruit), the dominant phenological driver is
 * whether the accumulated winter chill reaches the crop's requirement: a large
 * chill deficit threatens dormancy break and flowering consistency (alternate
 * bearing). A concurrent warm anomaly worsens chill fulfilment, and a falling
 * NDVI signals declining vigor. Risk is reported on the shared
 * {@link ClimateRiskLevel} scale (LOW / MODERATE / HIGH), with {@code UNKNOWN}
 * when there is no chill signal to evaluate.
 * </p>
 *
 * <p>
 * The chill ratio is a deliberate v1 proxy: it compares accumulated chill to the
 * crop requirement without weighting by how far the chill season has progressed.
 * It can be refined once a season-progress signal is available.
 * </p>
 */
@Service
public class PhenologicalRiskEvaluator {

    /** Below this chill-fulfilment ratio the dormancy/flowering risk is high. */
    static final double SEVERE_CHILL_RATIO = 0.40;
    /** Below this ratio the risk is at least moderate. */
    static final double MODERATE_CHILL_RATIO = 0.70;
    /** Warm anomaly (°C) that escalates a partial chill deficit to high risk. */
    static final double WARM_ANOMALY_HIGH_CELSIUS = 3.0;
    /** Warm anomaly (°C) that on its own raises the risk to moderate. */
    static final double WARM_ANOMALY_MODERATE_CELSIUS = 2.0;

    /**
     * Evaluates phenological risk for a plot.
     *
     * <p>
     * The chill-deficit component only contributes when {@code inChillRiskWindow}
     * is true (late dormancy through flowering); outside that window a low chill
     * reading is the normal off-season state and must not raise risk. Warm
     * anomalies and a declining NDVI remain secondary signals year-round, capped
     * at moderate.
     * </p>
     *
     * @param chillPortions Accumulated chill portions, or null when unavailable.
     * @param chillRequirement The crop's chill requirement in portions (> 0).
     * @param temperatureAnomalyCelsius Warm/cold anomaly vs reference, or null.
     * @param ndviFalling Whether the NDVI trend is declining.
     * @param inChillRiskWindow Whether a chill deficit is phenologically relevant now.
     * @return The phenological risk level, {@code UNKNOWN} when chill is missing.
     */
    public ClimateRiskLevel evaluate(
            Double chillPortions,
            double chillRequirement,
            Double temperatureAnomalyCelsius,
            boolean ndviFalling,
            boolean inChillRiskWindow
    ) {
        if (chillPortions == null || chillRequirement <= 0.0) {
            return ClimateRiskLevel.UNKNOWN;
        }

        double chillRatio = chillPortions / chillRequirement;
        double anomaly = temperatureAnomalyCelsius == null ? 0.0 : temperatureAnomalyCelsius;

        if (inChillRiskWindow) {
            if (chillRatio < SEVERE_CHILL_RATIO
                    || (anomaly >= WARM_ANOMALY_HIGH_CELSIUS && chillRatio < MODERATE_CHILL_RATIO)) {
                return ClimateRiskLevel.HIGH;
            }

            if (chillRatio < MODERATE_CHILL_RATIO) {
                return ClimateRiskLevel.MODERATE;
            }
        }

        if (anomaly >= WARM_ANOMALY_MODERATE_CELSIUS || ndviFalling) {
            return ClimateRiskLevel.MODERATE;
        }

        return ClimateRiskLevel.LOW;
    }
}
