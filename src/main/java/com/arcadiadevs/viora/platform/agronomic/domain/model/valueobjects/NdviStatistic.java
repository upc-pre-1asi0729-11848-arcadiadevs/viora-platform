package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.Instant;

/**
 * A single NDVI statistic point from a vegetation-index time series.
 *
 * <p>
 * Mirrors the per-capture statistics AgroMonitoring returns for a polygon:
 * the mean is always present, while the distribution descriptors are optional.
 * NDVI values are bounded to the {@code [-1, 1]} domain range.
 * </p>
 *
 * @param timestamp Capture instant of the statistic.
 * @param mean Mean NDVI for the plot at this capture.
 * @param minimum Minimum NDVI, or null when not provided.
 * @param maximum Maximum NDVI, or null when not provided.
 * @param median Median NDVI, or null when not provided.
 * @param standardDeviation NDVI standard deviation, or null when not provided.
 * @param percentile25 25th-percentile NDVI, or null when not provided.
 * @param percentile75 75th-percentile NDVI, or null when not provided.
 */
public record NdviStatistic(
        Instant timestamp,
        double mean,
        Double minimum,
        Double maximum,
        Double median,
        Double standardDeviation,
        Double percentile25,
        Double percentile75
) {
    public NdviStatistic {
        if (timestamp == null) {
            throw new IllegalArgumentException("NDVI statistic timestamp is required.");
        }
        requireNdviRange("Mean", mean);
        requireOptionalNdviRange("Minimum", minimum);
        requireOptionalNdviRange("Maximum", maximum);
        requireOptionalNdviRange("Median", median);
        requireOptionalNdviRange("25th percentile", percentile25);
        requireOptionalNdviRange("75th percentile", percentile75);
        if (standardDeviation != null
                && (!Double.isFinite(standardDeviation) || standardDeviation < 0)) {
            throw new IllegalArgumentException("NDVI standard deviation must be non-negative and finite.");
        }
    }

    private static void requireNdviRange(String label, double value) {
        if (!Double.isFinite(value) || value < -1.0 || value > 1.0) {
            throw new IllegalArgumentException(label + " NDVI must be between -1 and 1.");
        }
    }

    private static void requireOptionalNdviRange(String label, Double value) {
        if (value != null) {
            requireNdviRange(label, value);
        }
    }
}
