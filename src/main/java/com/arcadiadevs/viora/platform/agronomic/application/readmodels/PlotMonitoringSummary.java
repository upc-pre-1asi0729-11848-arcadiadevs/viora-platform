package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrend;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Real-time, per-plot monitoring projection for the plot monitoring screen.
 *
 * <p>
 * Assembled on demand from live external sources (satellite NDVI and weather)
 * consolidated with persisted statistics, this read model degrades gracefully:
 * every monitoring signal is optional and the per-source {@link DataSourceMetadata}
 * reports why a signal may be missing. It is deliberately not the persisted
 * {@code MonitoringSummary} aggregate, which is user-scoped and stored.
 * </p>
 *
 * @param plot The plot aggregate.
 * @param currentNdvi Consolidated current NDVI (satellite first, latest persisted statistic as fallback), or null.
 * @param ndviTrend NDVI trend over the recent window, or null when no history exists.
 * @param chillPortions Latest persisted chill portions reading, or null when none exists.
 * @param chillPortionsWeeklyDelta Chill portions accumulated over the last week (latest minus the reading ~7 days earlier), or null when there is not enough history.
 * @param chillRequirement The plot's resolved chill requirement (value, source and model).
 * @param healthStatus Consolidated health badge derived from the current NDVI.
 * @param phenologicalRisk Phenological risk derived from chill fulfilment, weather anomaly and NDVI trend.
 * @param yieldForecastTonnes Estimated yield in tonnes, or null when NDVI is unavailable.
 * @param weather Current weather snapshot, or null when unavailable.
 * @param climateRiskLevel Consolidated climate risk, or null when it cannot be determined.
 * @param lastUpdatedAt Instant of the most recent monitoring signal, or null when none exists.
 * @param recommendations Mitigation recommendations derived from the climate risk.
 * @param climateSource Weather source identity, availability and freshness.
 * @param ndviSource Satellite NDVI source identity, availability and freshness.
 */
public record PlotMonitoringSummary(
        Plot plot,
        Double currentNdvi,
        NdviTrend ndviTrend,
        Double chillPortions,
        Double chillPortionsWeeklyDelta,
        ChillRequirement chillRequirement,
        GeneralHealthStatus healthStatus,
        ClimateRiskLevel phenologicalRisk,
        Double yieldForecastTonnes,
        WeatherSnapshot weather,
        ClimateRiskLevel climateRiskLevel,
        Instant lastUpdatedAt,
        List<MitigationRecommendation> recommendations,
        DataSourceMetadata climateSource,
        DataSourceMetadata ndviSource
) {
    public PlotMonitoringSummary {
        Objects.requireNonNull(plot, "Plot is required.");
        Objects.requireNonNull(healthStatus, "Health status is required.");
        Objects.requireNonNull(phenologicalRisk, "Phenological risk is required.");
        Objects.requireNonNull(climateSource, "Climate source metadata is required.");
        Objects.requireNonNull(ndviSource, "NDVI source metadata is required.");
        recommendations = recommendations == null ? List.of() : List.copyOf(recommendations);
    }
}
