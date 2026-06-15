package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import org.springframework.stereotype.Service;

/**
 * Domain service that derives a consolidated plot health badge from NDVI vigor.
 *
 * <p>
 * Centralizes the NDVI-to-health thresholds so the value is consistent across
 * monitoring projections. A missing NDVI yields {@code UNKNOWN} rather than a
 * fabricated health status.
 * </p>
 *
 * <p>
 * The "healthy" cut-off is crop-aware: olive groves have a structurally lower
 * canopy NDVI (sparse crowns with bare soil between trees), so the generic 0.60
 * threshold would label vigorous olives as WARNING. Olives therefore use a
 * lower healthy threshold while the critical floor stays shared.
 * </p>
 */
@Service
public class PlotHealthEvaluator {

    static final double CRITICAL_NDVI_THRESHOLD = 0.30;
    static final double WARNING_NDVI_THRESHOLD = 0.60;
    static final double OLIVE_WARNING_NDVI_THRESHOLD = 0.45;

    /**
     * Derives the health status implied by a consolidated NDVI value using the
     * generic (non crop-specific) thresholds.
     *
     * @param ndvi Consolidated NDVI, or null when no vegetation signal exists.
     * @return The health badge, {@code UNKNOWN} when NDVI is unavailable.
     */
    public GeneralHealthStatus evaluate(Double ndvi) {
        return evaluate(ndvi, null);
    }

    /**
     * Derives the health status implied by a consolidated NDVI value, applying
     * crop-aware "healthy" thresholds.
     *
     * @param ndvi Consolidated NDVI, or null when no vegetation signal exists.
     * @param cropType The plot crop type, used to pick the healthy threshold.
     * @return The health badge, {@code UNKNOWN} when NDVI is unavailable.
     */
    public GeneralHealthStatus evaluate(Double ndvi, String cropType) {
        if (ndvi == null) {
            return GeneralHealthStatus.UNKNOWN;
        }
        if (ndvi < CRITICAL_NDVI_THRESHOLD) {
            return GeneralHealthStatus.CRITICAL;
        }
        if (ndvi < warningThresholdFor(cropType)) {
            return GeneralHealthStatus.WARNING;
        }
        return GeneralHealthStatus.HEALTHY;
    }

    private double warningThresholdFor(String cropType) {
        return isOlive(cropType) ? OLIVE_WARNING_NDVI_THRESHOLD : WARNING_NDVI_THRESHOLD;
    }

    private boolean isOlive(String cropType) {
        return cropType != null && cropType.trim().toLowerCase().contains("oliv");
    }
}
