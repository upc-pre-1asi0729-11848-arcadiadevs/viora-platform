package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.util.List;

/**
 * Chart-oriented REST projection for agronomic statistic trends.
 *
 * <p>
 * Exposes parallel series aligned with {@code labels} (the chart x-axis), the
 * chill-portions reference {@code threshold}, an overall NDVI-driven trend with
 * a status label and observation, and detailed per-metric trends versus the
 * previous comparable period.
 * </p>
 */
public record AgronomicStatisticSeriesResource(
        Long plotId,
        String timeRange,
        List<String> labels,
        List<Double> ndviSeries,
        List<Double> cpSeries,
        List<Double> chillHoursSeries,
        Double threshold,
        String chillRequirementSource,
        String chillMetricModel,
        String chillUnit,
        String trend,
        String statusLabel,
        String observation,
        MetricTrendResource ndviTrend,
        MetricTrendResource chillPortionsTrend,
        MetricTrendResource chillHoursTrend
) {

    public record MetricTrendResource(
            Double currentValue,
            Double previousValue,
            Double change,
            Double changePercent,
            String direction
    ) {
    }
}
