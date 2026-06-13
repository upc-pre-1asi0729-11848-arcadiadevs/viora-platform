package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

/**
 * REST projection for the real-time, per-plot monitoring summary screen.
 *
 * <p>
 * Optional monitoring signals are nullable so the client can render "not
 * available" states, and each {@link DataSourceResource} carries the
 * availability and freshness of its external source.
 * </p>
 */
public record PlotMonitoringSummaryResource(
        Long plotId,
        Long userId,
        String plotName,
        Double currentNdvi,
        NdviTrendResource ndviTrend,
        Double chillPortions,
        Double chillRequirementPortions,
        String chillRequirementSource,
        String chillMetricModel,
        String chillUnit,
        String healthStatus,
        Double yieldForecastTonnes,
        WeatherSnapshotResource weather,
        String climateRiskLevel,
        Instant lastUpdatedAt,
        List<MitigationRecommendationResource> recommendations,
        DataSourceResource climateSource,
        DataSourceResource ndviSource
) {

    public record NdviTrendResource(
            String direction,
            double changeRate,
            List<NdviPointResource> series
    ) {
    }

    public record NdviPointResource(
            Instant timestamp,
            double mean,
            Double minimum,
            Double maximum,
            Double median
    ) {
    }

    public record DataSourceResource(
            String provider,
            String availability,
            Instant lastReadingAt,
            Integer updateFrequencyMinutes
    ) {
    }
}
