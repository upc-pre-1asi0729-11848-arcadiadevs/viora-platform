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
 */
@Service
public class PlotHealthEvaluator {

    static final double CRITICAL_NDVI_THRESHOLD = 0.30;
    static final double WARNING_NDVI_THRESHOLD = 0.60;

    /**
     * Derives the health status implied by a consolidated NDVI value.
     *
     * @param ndvi Consolidated NDVI, or null when no vegetation signal exists.
     * @return The health badge, {@code UNKNOWN} when NDVI is unavailable.
     */
    public GeneralHealthStatus evaluate(Double ndvi) {
        if (ndvi == null) {
            return GeneralHealthStatus.UNKNOWN;
        }
        if (ndvi < CRITICAL_NDVI_THRESHOLD) {
            return GeneralHealthStatus.CRITICAL;
        }
        if (ndvi < WARNING_NDVI_THRESHOLD) {
            return GeneralHealthStatus.WARNING;
        }
        return GeneralHealthStatus.HEALTHY;
    }
}
