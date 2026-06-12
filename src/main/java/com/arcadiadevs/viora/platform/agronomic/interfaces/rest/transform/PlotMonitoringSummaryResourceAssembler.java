package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotMonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrend;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MitigationRecommendationResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotMonitoringSummaryResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.WeatherSnapshotResource;

import java.util.List;

/**
 * Maps the per-plot monitoring summary projection to its REST resource.
 */
public final class PlotMonitoringSummaryResourceAssembler {

    private PlotMonitoringSummaryResourceAssembler() {
    }

    public static PlotMonitoringSummaryResource toResourceFromReadModel(PlotMonitoringSummary summary) {
        var plot = summary.plot();
        return new PlotMonitoringSummaryResource(
                plot.getId().getValue(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                summary.currentNdvi(),
                toNdviTrendResource(summary.ndviTrend()),
                summary.chillPortions(),
                summary.healthStatus().name(),
                summary.yieldForecastTonnes(),
                toWeatherResource(summary.weather()),
                summary.climateRiskLevel() == null ? null : summary.climateRiskLevel().name(),
                summary.lastUpdatedAt(),
                toRecommendationResources(summary.recommendations()),
                toDataSourceResource(summary.climateSource()),
                toDataSourceResource(summary.ndviSource())
        );
    }

    private static PlotMonitoringSummaryResource.NdviTrendResource toNdviTrendResource(NdviTrend trend) {
        if (trend == null) {
            return null;
        }
        return new PlotMonitoringSummaryResource.NdviTrendResource(
                trend.direction().name(),
                trend.changeRate(),
                trend.series().stream()
                        .map(PlotMonitoringSummaryResourceAssembler::toNdviPointResource)
                        .toList()
        );
    }

    private static PlotMonitoringSummaryResource.NdviPointResource toNdviPointResource(NdviStatistic statistic) {
        return new PlotMonitoringSummaryResource.NdviPointResource(
                statistic.timestamp(),
                statistic.mean(),
                statistic.minimum(),
                statistic.maximum(),
                statistic.median()
        );
    }

    private static WeatherSnapshotResource toWeatherResource(WeatherSnapshot weather) {
        if (weather == null) {
            return null;
        }
        return new WeatherSnapshotResource(
                weather.getWeatherStatus().name(),
                weather.getMeasurementDate().getValue(),
                weather.getClimateRiskLevel().name(),
                weather.getTemperature()
        );
    }

    private static List<MitigationRecommendationResource> toRecommendationResources(
            List<MitigationRecommendation> recommendations
    ) {
        return recommendations.stream()
                .map(recommendation -> new MitigationRecommendationResource(
                        recommendation.getActionType().name(),
                        recommendation.getNutritionInputRecommendation().getValue(),
                        recommendation.getApplicationWindow().getStartDate(),
                        recommendation.getApplicationWindow().getEndDate()
                ))
                .toList();
    }

    private static PlotMonitoringSummaryResource.DataSourceResource toDataSourceResource(
            DataSourceMetadata metadata
    ) {
        return new PlotMonitoringSummaryResource.DataSourceResource(
                metadata.provider(),
                metadata.availability().name(),
                metadata.lastReadingAt(),
                metadata.updateFrequencyMinutes()
        );
    }
}
