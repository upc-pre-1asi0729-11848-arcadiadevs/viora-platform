package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotMonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillSeasonEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.MitigationRecommendationGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.NdviTrendAnalyzer;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PhenologicalRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotHealthEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.YieldForecastEstimator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrend;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrendDirection;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.TimeRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Query service for the real-time, per-plot monitoring summary.
 *
 * <p>
 * Consolidates live external signals (satellite NDVI and weather), persisted
 * agronomic statistics (chill, NDVI fallback), the derived health badge, the
 * NDVI trend, climate risk and mitigation recommendations, plus the freshness
 * of each external source. Missing signals degrade gracefully instead of
 * failing the request.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PlotMonitoringSummaryQueryService {

    private static final TimeRange MONITORING_WINDOW = TimeRange.LAST_90_DAYS;

    private final PlotRepository plotRepository;
    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final AgroMonitoringImageryService agroMonitoringImageryService;
    private final WeatherDataService weatherDataService;
    private final NdviTrendAnalyzer ndviTrendAnalyzer;
    private final PlotHealthEvaluator plotHealthEvaluator;
    private final PhenologicalRiskEvaluator phenologicalRiskEvaluator;
    private final ChillSeasonEvaluator chillSeasonEvaluator;
    private final ClimateRiskEvaluator climateRiskEvaluator;
    private final MitigationRecommendationGenerator mitigationRecommendationGenerator;
    private final YieldForecastEstimator yieldForecastEstimator;
    private final ChillRequirementResolver chillRequirementResolver;
    private final DynamicNutritionPolicy dynamicNutritionPolicy;

    /**
     * Handles the per-plot monitoring summary query.
     *
     * @param query Query carrying the owner and plot identifiers.
     * @return The monitoring summary projection, or an ownership/not-found error.
     */
    @Transactional
    public Result<PlotMonitoringSummary, ApplicationError> handle(GetPlotMonitoringSummaryQuery query) {
        var userId = new UserId(query.userId());
        var plotId = new PlotId(query.plotId());

        var plotOptional = plotRepository.findById(plotId);
        if (plotOptional.isEmpty() || !plotOptional.get().isActive()) {
            return Result.failure(ApplicationError.notFound("plot", query.plotId().toString()));
        }

        var plot = plotOptional.get();
        if (!plot.belongsTo(userId)) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(query.userId(), query.plotId())
            ));
        }

        var window = MONITORING_WINDOW.toDateRange(LocalDate.now());

        var imagery = agroMonitoringImageryService.findCurrentImagery(plot);
        var weather = weatherDataService.getCurrentWeatherSnapshot(plot);
        var ndviTrend = agroMonitoringImageryService.findNdviHistory(plot, window)
                .map(ndviTrendAnalyzer::analyze)
                .orElse(null);

        var latestStatistic = latestStatistic(userId, plotId, window);
        var currentNdvi = consolidateNdvi(imagery, latestStatistic);
        var chillPortions = latestStatistic
                .map(statistic -> statistic.getChillPortions().getValue())
                .orElse(null);

        var healthStatus = plotHealthEvaluator.evaluate(currentNdvi, plot.getCropType());
        var chillRequirement = chillRequirementResolver.resolveFor(plot);
        var inChillRiskWindow = chillSeasonEvaluator.isInChillRiskWindow(
                plot.getPolygonCoordinates().centroid().getLatitude(), LocalDate.now());
        var phenologicalRisk = phenologicalRiskEvaluator.evaluate(
                chillPortions,
                chillRequirement.value(),
                weather.map(snapshot -> snapshot.getTemperature()
                        - dynamicNutritionPolicy.temperatureReferenceCelsius()).orElse(null),
                ndviTrend != null && ndviTrend.direction() == NdviTrendDirection.FALLING,
                inChillRiskWindow
        );
        var yieldForecastTonnes = estimateYield(plot, currentNdvi, chillPortions, chillRequirement);
        var climateRiskLevel = resolveClimateRisk(weather, currentNdvi);
        var recommendations = climateRiskLevel == null
                ? List.<MitigationRecommendation>of()
                : mitigationRecommendationGenerator.generateRecommendations(climateRiskLevel);

        var lastUpdatedAt = resolveLastUpdatedAt(imagery, weather, latestStatistic, ndviTrend);

        return Result.success(new PlotMonitoringSummary(
                plot,
                currentNdvi,
                ndviTrend,
                chillPortions,
                chillRequirement,
                healthStatus,
                phenologicalRisk,
                yieldForecastTonnes,
                weather.orElse(null),
                climateRiskLevel,
                lastUpdatedAt,
                recommendations,
                weatherDataService.describeSource(plot),
                agroMonitoringImageryService.describeNdviSource(plot)
        ));
    }

    /* Yield estimate requires a vegetation signal; chill defaults to zero adequacy. */
    private Double estimateYield(
            Plot plot,
            Double currentNdvi,
            Double chillPortions,
            ChillRequirement chillRequirement
    ) {
        if (currentNdvi == null) {
            return null;
        }
        return yieldForecastEstimator.estimate(
                currentNdvi,
                chillPortions == null ? 0.0 : chillPortions,
                chillRequirement.value(),
                plot.getAreaSize().getHectares().doubleValue()
        ).getValue();
    }

    private Optional<AgronomicStatistic> latestStatistic(UserId userId, PlotId plotId, DateRange window) {
        return agronomicStatisticRepository
                .findAllByUserIdAndPlotIdAndMeasurementDateBetween(userId, plotId, window)
                .stream()
                .max(Comparator.comparing(statistic -> statistic.getMeasurementDate().getValue()));
    }

    /* Prefers the real satellite NDVI; falls back to the latest persisted statistic. */
    private Double consolidateNdvi(
            Optional<SatelliteImagery> imagery,
            Optional<AgronomicStatistic> latestStatistic
    ) {
        return imagery
                .map(SatelliteImagery::ndviMean)
                .filter(Objects::nonNull)
                .or(() -> latestStatistic.map(statistic -> statistic.getNdviValue().getValue()))
                .orElse(null);
    }

    /* Consolidated risk needs NDVI and weather; otherwise fall back to weather alone. */
    private ClimateRiskLevel resolveClimateRisk(
            Optional<WeatherSnapshot> weather,
            Double currentNdvi
    ) {
        if (weather.isEmpty()) {
            return null;
        }

        if (currentNdvi != null) {
            return climateRiskEvaluator.evaluateClimateRisk(
                    new NdviValue(currentNdvi),
                    weather.get(),
                    dynamicNutritionPolicy
            );
        }

        return weather.get().getClimateRiskLevel();
    }

    private Instant resolveLastUpdatedAt(
            Optional<SatelliteImagery> imagery,
            Optional<WeatherSnapshot> weather,
            Optional<AgronomicStatistic> latestStatistic,
            NdviTrend ndviTrend
    ) {
        var candidates = new ArrayList<Instant>();
        imagery.map(SatelliteImagery::captureDate).ifPresent(candidates::add);
        weather.map(snapshot -> atStartOfDay(snapshot.getMeasurementDate().getValue())).ifPresent(candidates::add);
        latestStatistic.map(statistic -> atStartOfDay(statistic.getMeasurementDate().getValue()))
                .ifPresent(candidates::add);
        if (ndviTrend != null) {
            candidates.add(ndviTrend.series().getLast().timestamp());
        }

        return candidates.stream().max(Comparator.naturalOrder()).orElse(null);
    }

    private Instant atStartOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
