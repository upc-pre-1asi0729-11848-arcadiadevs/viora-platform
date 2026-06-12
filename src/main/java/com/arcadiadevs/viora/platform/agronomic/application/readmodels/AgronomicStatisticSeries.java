package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Chart-oriented projection of agronomic statistics over a time window.
 *
 * <p>
 * Holds the ordered points of the current window plus period-over-period trends
 * for each metric (current window average vs the immediately preceding window of
 * equal length). The chill-portions threshold is exposed as the chart reference
 * line.
 * </p>
 *
 * @param plotId Plot filter, or null when aggregated across the user's plots.
 * @param timeRange The resolved time range.
 * @param points Ordered statistic points within the current window.
 * @param ndviTrend NDVI trend vs the previous period.
 * @param chillPortionsTrend Chill-portions trend vs the previous period.
 * @param chillHoursTrend Chill-hours trend vs the previous period.
 * @param chillPortionsThreshold Chill-portions reference threshold for the chart.
 */
public record AgronomicStatisticSeries(
        Long plotId,
        TimeRange timeRange,
        List<Point> points,
        MetricTrend ndviTrend,
        MetricTrend chillPortionsTrend,
        MetricTrend chillHoursTrend,
        double chillPortionsThreshold
) {
    public AgronomicStatisticSeries {
        Objects.requireNonNull(timeRange, "Time range is required.");
        Objects.requireNonNull(ndviTrend, "NDVI trend is required.");
        Objects.requireNonNull(chillPortionsTrend, "Chill portions trend is required.");
        Objects.requireNonNull(chillHoursTrend, "Chill hours trend is required.");
        points = points == null ? List.of() : List.copyOf(points);
    }

    /**
     * A single charted point.
     *
     * @param date Measurement date.
     * @param ndvi NDVI value.
     * @param chillPortions Accumulated chill portions.
     * @param chillHours Accumulated chill hours.
     */
    public record Point(LocalDate date, double ndvi, double chillPortions, double chillHours) {
    }
}
