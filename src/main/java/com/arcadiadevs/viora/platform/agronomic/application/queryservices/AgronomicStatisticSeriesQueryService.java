package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.AgronomicStatisticSeries;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.MetricTrend;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.TrendDirection;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticSeriesQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Query service that builds chart-oriented agronomic statistic series with
 * period-over-period trends.
 *
 * <p>
 * Points within the selected window are aggregated by date (averaged across the
 * user's plots when no plot filter is given). Each metric's trend compares the
 * current window average against the immediately preceding window of equal
 * length, so the client can show the difference relative to the previous period.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AgronomicStatisticSeriesQueryService {

    private static final double NDVI_STABILITY_EPSILON = 0.02;
    private static final double CHILL_STABILITY_EPSILON = 1.0;

    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final PlotRepository plotRepository;
    private final ChillRequirementResolver chillRequirementResolver;

    @Transactional(readOnly = true)
    public Result<AgronomicStatisticSeries, ApplicationError> handle(GetAgronomicStatisticSeriesQuery query) {
        try {
            var userId = new UserId(query.userId());
            var authenticatedUserId = new UserId(query.authenticatedUserId());

            if (!userId.equals(authenticatedUserId)) {
                return Result.failure(ApplicationError.forbidden(
                        "agronomic-statistics-access",
                        "Authenticated user cannot access statistics from another user."
                ));
            }

            PlotId plotId = null;
            ChillRequirement chillRequirement = chillRequirementResolver.resolveDefault();
            if (query.plotId() != null) {
                plotId = new PlotId(query.plotId());
                var plot = plotRepository.findById(plotId);
                if (plot.isEmpty() || !plot.get().isActive()) {
                    return Result.failure(ApplicationError.validationError(
                            "plotId",
                            "The selected plot does not exist or is inactive."
                    ));
                }
                if (!plot.get().belongsTo(userId)) {
                    return Result.failure(ApplicationError.forbidden(
                            "plot-ownership",
                            "User %d does not own plot %d.".formatted(query.userId(), query.plotId())
                    ));
                }
                chillRequirement = chillRequirementResolver.resolveFor(plot.get());
            }

            var today = LocalDate.now();
            var currentWindow = query.timeRange().toDateRange(today);
            long windowDays = currentWindow.getStartDate().until(currentWindow.getEndDate()).getDays() + 1L;
            var previousEnd = currentWindow.getStartDate().minusDays(1);
            var previousStart = previousEnd.minusDays(windowDays - 1);
            var combinedRange = new DateRange(previousStart, currentWindow.getEndDate());

            var statistics = fetch(userId, plotId, combinedRange);
            var currentStatistics = within(statistics, currentWindow.getStartDate(), currentWindow.getEndDate());
            var previousStatistics = within(statistics, previousStart, previousEnd);

            var series = new AgronomicStatisticSeries(
                    query.plotId(),
                    query.timeRange(),
                    aggregatePoints(currentStatistics),
                    trend(currentStatistics, previousStatistics, AgronomicStatisticSeriesQueryService::ndvi, NDVI_STABILITY_EPSILON),
                    trend(currentStatistics, previousStatistics, AgronomicStatisticSeriesQueryService::chillPortions, CHILL_STABILITY_EPSILON),
                    trend(currentStatistics, previousStatistics, AgronomicStatisticSeriesQueryService::chillHours, CHILL_STABILITY_EPSILON),
                    chillRequirement.value(),
                    chillRequirement.source(),
                    chillRequirement.model()
            );

            return Result.success(series);
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "agronomic-statistics-series",
                    exception.getMessage()
            ));
        }
    }

    private List<AgronomicStatistic> fetch(UserId userId, PlotId plotId, DateRange range) {
        return plotId == null
                ? agronomicStatisticRepository.findAllByUserIdAndMeasurementDateBetween(userId, range)
                : agronomicStatisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(userId, plotId, range);
    }

    private List<AgronomicStatistic> within(List<AgronomicStatistic> statistics, LocalDate start, LocalDate end) {
        return statistics.stream()
                .filter(statistic -> {
                    var date = statistic.getMeasurementDate().getValue();
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .toList();
    }

    /* Averages metrics per date so the chart has one point per day even across plots. */
    private List<AgronomicStatisticSeries.Point> aggregatePoints(List<AgronomicStatistic> statistics) {
        return statistics.stream()
                .collect(Collectors.groupingBy(
                        statistic -> statistic.getMeasurementDate().getValue(),
                        TreeMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> new AgronomicStatisticSeries.Point(
                        entry.getKey(),
                        round(average(entry.getValue(), AgronomicStatisticSeriesQueryService::ndvi)),
                        round(average(entry.getValue(), AgronomicStatisticSeriesQueryService::chillPortions)),
                        round(average(entry.getValue(), AgronomicStatisticSeriesQueryService::chillHours))
                ))
                .toList();
    }

    private MetricTrend trend(
            List<AgronomicStatistic> current,
            List<AgronomicStatistic> previous,
            ToDoubleFunction<AgronomicStatistic> metric,
            double epsilon
    ) {
        Double currentValue = current.isEmpty() ? null : round(average(current, metric));
        Double previousValue = previous.isEmpty() ? null : round(average(previous, metric));

        if (currentValue == null && previousValue == null) {
            return MetricTrend.stableUnknown();
        }

        Double change = (currentValue != null && previousValue != null)
                ? round(currentValue - previousValue)
                : null;
        Double changePercent = (change != null && previousValue != null && previousValue != 0.0)
                ? round(change / previousValue * 100.0)
                : null;

        return new MetricTrend(currentValue, previousValue, change, changePercent, direction(change, epsilon));
    }

    private TrendDirection direction(Double change, double epsilon) {
        if (change == null || Math.abs(change) <= epsilon) {
            return TrendDirection.STABLE;
        }
        return change > 0 ? TrendDirection.UP : TrendDirection.DOWN;
    }

    private double average(List<AgronomicStatistic> statistics, ToDoubleFunction<AgronomicStatistic> metric) {
        return statistics.stream().mapToDouble(metric).average().orElse(0.0);
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private static double ndvi(AgronomicStatistic statistic) {
        return statistic.getNdviValue().getValue();
    }

    private static double chillPortions(AgronomicStatistic statistic) {
        return statistic.getChillPortions().getValue();
    }

    private static double chillHours(AgronomicStatistic statistic) {
        return statistic.getChillHours().getValue();
    }
}
