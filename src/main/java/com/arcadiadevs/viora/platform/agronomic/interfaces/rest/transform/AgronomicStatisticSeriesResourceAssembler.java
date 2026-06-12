package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.AgronomicStatisticSeries;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.MetricTrend;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.TrendDirection;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.AgronomicStatisticSeriesResource;

import java.util.List;

/**
 * Maps the agronomic statistic series projection to its chart-oriented REST resource.
 */
public final class AgronomicStatisticSeriesResourceAssembler {

    private AgronomicStatisticSeriesResourceAssembler() {
    }

    public static AgronomicStatisticSeriesResource toResourceFromReadModel(AgronomicStatisticSeries series) {
        var points = series.points();
        return new AgronomicStatisticSeriesResource(
                series.plotId(),
                series.timeRange().name(),
                points.stream().map(point -> point.date().toString()).toList(),
                points.stream().map(AgronomicStatisticSeries.Point::ndvi).toList(),
                points.stream().map(AgronomicStatisticSeries.Point::chillPortions).toList(),
                points.stream().map(AgronomicStatisticSeries.Point::chillHours).toList(),
                series.chillPortionsThreshold(),
                trendLabel(series.ndviTrend().direction()),
                trendLabel(series.ndviTrend().direction()),
                observation(series),
                toMetricTrendResource(series.ndviTrend()),
                toMetricTrendResource(series.chillPortionsTrend()),
                toMetricTrendResource(series.chillHoursTrend())
        );
    }

    private static AgronomicStatisticSeriesResource.MetricTrendResource toMetricTrendResource(MetricTrend trend) {
        return new AgronomicStatisticSeriesResource.MetricTrendResource(
                trend.currentValue(),
                trend.previousValue(),
                trend.change(),
                trend.changePercent(),
                trend.direction().name()
        );
    }

    private static String trendLabel(TrendDirection direction) {
        return switch (direction) {
            case UP -> "Up";
            case DOWN -> "Down";
            case STABLE -> "Stable";
        };
    }

    private static String observation(AgronomicStatisticSeries series) {
        if (series.points().isEmpty()) {
            return "No agronomic statistics recorded for this period yet.";
        }
        return "Vegetation vigor (NDVI) is %s over the %s.".formatted(
                directionWord(series.ndviTrend().direction()),
                rangeLabel(series.timeRange())
        );
    }

    private static String directionWord(TrendDirection direction) {
        return switch (direction) {
            case UP -> "rising";
            case DOWN -> "falling";
            case STABLE -> "stable";
        };
    }

    private static String rangeLabel(TimeRange timeRange) {
        return switch (timeRange) {
            case LAST_7_DAYS -> "last 7 days";
            case LAST_30_DAYS -> "last 30 days";
            case LAST_90_DAYS -> "last 90 days";
            case LAST_180_DAYS -> "last 180 days";
            case LAST_365_DAYS -> "last 365 days";
            case CAMPAIGN -> "current campaign";
        };
    }
}
