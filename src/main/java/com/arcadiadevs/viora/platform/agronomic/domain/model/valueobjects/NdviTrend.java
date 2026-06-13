package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.List;

/**
 * NDVI trend over a time window, derived from a historical statistics series.
 *
 * @param direction Whether vegetation vigor is rising, falling or stable.
 * @param changeRate Net NDVI change across the window (latest mean minus earliest mean).
 * @param series The ordered NDVI statistics the trend was computed from; never empty.
 */
public record NdviTrend(
        NdviTrendDirection direction,
        double changeRate,
        List<NdviStatistic> series
) {
    public NdviTrend {
        if (direction == null) {
            throw new IllegalArgumentException("NDVI trend direction is required.");
        }
        if (!Double.isFinite(changeRate)) {
            throw new IllegalArgumentException("NDVI trend change rate must be finite.");
        }
        if (series == null || series.isEmpty()) {
            throw new IllegalArgumentException("NDVI trend series must contain at least one statistic.");
        }
        series = List.copyOf(series);
    }
}
