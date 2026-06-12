package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrend;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrendDirection;
import org.springframework.stereotype.Service;

/**
 * Domain service that classifies the NDVI trend of a historical series.
 *
 * <p>
 * The trend compares the earliest and latest NDVI means in the window. Changes
 * within {@link #STABILITY_THRESHOLD} are treated as stable, since small NDVI
 * fluctuations are noise rather than a meaningful vigor shift.
 * </p>
 */
@Service
public class NdviTrendAnalyzer {

    /** NDVI change magnitude below which the trend is considered stable. */
    static final double STABILITY_THRESHOLD = 0.02;

    /**
     * Analyzes a non-empty NDVI history into a classified trend.
     *
     * @param history The NDVI statistics series (ordered by timestamp).
     * @return The classified NDVI trend.
     */
    public NdviTrend analyze(NdviHistory history) {
        if (history == null) {
            throw new IllegalArgumentException("NDVI history is required.");
        }

        var statistics = history.statistics();
        double earliest = statistics.getFirst().mean();
        double latest = statistics.getLast().mean();
        double changeRate = latest - earliest;

        NdviTrendDirection direction;
        if (changeRate > STABILITY_THRESHOLD) {
            direction = NdviTrendDirection.RISING;
        } else if (changeRate < -STABILITY_THRESHOLD) {
            direction = NdviTrendDirection.FALLING;
        } else {
            direction = NdviTrendDirection.STABLE;
        }

        return new NdviTrend(direction, changeRate, statistics);
    }
}
