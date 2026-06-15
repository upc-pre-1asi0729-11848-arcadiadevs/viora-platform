package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetCurrentMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.MitigationRecommendationGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotHealthEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.YieldForecastEstimator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application query service for handling monitoring summary queries.
 *
 * <p>
 * This service coordinates the retrieval and consolidation of agronomic data
 * to provide a current monitoring summary for a specific user.
 * </p>
 */
@Service
public class MonitoringSummaryQueryService {

    private final PlotRepository plotRepository;
    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final WeatherDataService weatherDataService;
    private final ClimateRiskEvaluator climateRiskEvaluator;
    private final MitigationRecommendationGenerator mitigationRecommendationGenerator;
    private final YieldForecastEstimator yieldForecastEstimator;
    private final ChillRequirementResolver chillRequirementResolver;
    private final PlotHealthEvaluator plotHealthEvaluator;
    private final DynamicNutritionPolicy dynamicNutritionPolicy;

    public MonitoringSummaryQueryService(
            PlotRepository plotRepository,
            AgronomicStatisticRepository agronomicStatisticRepository,
            WeatherDataService weatherDataService,
            ClimateRiskEvaluator climateRiskEvaluator,
            MitigationRecommendationGenerator mitigationRecommendationGenerator,
            YieldForecastEstimator yieldForecastEstimator,
            ChillRequirementResolver chillRequirementResolver,
            PlotHealthEvaluator plotHealthEvaluator,
            DynamicNutritionPolicy dynamicNutritionPolicy
    ) {
        this.plotRepository = plotRepository;
        this.agronomicStatisticRepository = agronomicStatisticRepository;
        this.weatherDataService = weatherDataService;
        this.climateRiskEvaluator = climateRiskEvaluator;
        this.mitigationRecommendationGenerator = mitigationRecommendationGenerator;
        this.yieldForecastEstimator = yieldForecastEstimator;
        this.chillRequirementResolver = chillRequirementResolver;
        this.plotHealthEvaluator = plotHealthEvaluator;
        this.dynamicNutritionPolicy = dynamicNutritionPolicy;
    }

    /**
     * Handles the GetCurrentMonitoringSummaryQuery to retrieve the current monitoring summary.
     *
     * @param query The query containing the user ID.
     * @return The MonitoringSummary for the user, or an empty Optional if no data is found.
     */
    public Optional<MonitoringSummary> handle(GetCurrentMonitoringSummaryQuery query) {
        UserId userId = query.getUserId();

        // 1. Get plots for the user
        List<Plot> userPlots = plotRepository.findByUserId(userId);
        if (userPlots.isEmpty()) {
            return Optional.empty(); // No plots, no summary
        }

        // 2. Define a date range for recent statistics (e.g., last 30 days)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        DateRange dateRange = new DateRange(startDate, endDate);

        // 3. Retrieve agronomic statistics for the user within the date range
        List<AgronomicStatistic> allUserStatistics = agronomicStatisticRepository.findAllByUserIdAndMeasurementDateBetween(
                userId, dateRange
        );

        if (allUserStatistics.isEmpty()) {
            return Optional.empty(); // No statistics, no summary
        }

        // 4. Consolidate the current state across plots: take each plot's latest
        // snapshot, then average across plots. Chill hours and chill portions are
        // cumulative metrics, so averaging across the whole 30-day window would
        // understate the current accumulation; the dashboard average must reflect
        // the latest reading per plot, consistent with the per-plot summary.
        List<AgronomicStatistic> latestPerPlot = allUserStatistics.stream()
                .collect(Collectors.groupingBy(
                        stat -> stat.getPlotId().getValue(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(
                                        stat -> stat.getMeasurementDate().getValue())),
                                Optional::orElseThrow)))
                .values()
                .stream()
                .toList();

        double consolidatedNdvi = latestPerPlot.stream()
                .mapToDouble(stat -> stat.getNdviValue().getValue())
                .average()
                .orElse(0.0); // Default if no NDVI values

        double consolidatedChillHours = latestPerPlot.stream()
                .mapToDouble(stat -> stat.getChillHours().getValue())
                .average()
                .orElse(0.0); // Default if no chill hours

        // 5. Determine GeneralHealthStatus and estimate yield (transparent heuristic).
        // The aggregate spans the user's plots; use the first plot's crop as the
        // representative threshold basis (crop-aware healthy cut-off).
        GeneralHealthStatus generalHealthStatus = plotHealthEvaluator.evaluate(
                consolidatedNdvi, userPlots.getFirst().getCropType());
        var latestStatisticByPlotId = latestPerPlot.stream()
                .collect(Collectors.toMap(
                        statistic -> statistic.getPlotId().getValue(),
                        statistic -> statistic
                ));
        double totalYieldTonnes = userPlots.stream()
                .mapToDouble(plot -> estimatePlotYield(
                        plot,
                        latestStatisticByPlotId.get(plot.getId().getValue())
                ))
                .sum();
        YieldForecast yieldForecast = new YieldForecast(totalYieldTonnes);

        // 6. Use the latest measurement date from the statistics, or current date if none
        MeasurementDate latestMeasurementDate = allUserStatistics.stream()
                .map(AgronomicStatistic::getMeasurementDate)
                .max(Comparator.comparing(MeasurementDate::getValue))
                .orElse(new MeasurementDate(LocalDate.now()));

        // 7. Get Weather Snapshot for a representative plot of the user
        Plot representativePlot = userPlots.getFirst();
        Optional<WeatherSnapshot> optionalWeatherSnapshot =
                weatherDataService.getCurrentWeatherSnapshot(representativePlot);
        WeatherSnapshot weatherSnapshot = optionalWeatherSnapshot.orElseThrow(
                () -> new IllegalStateException("Weather data not available for user " + userId.getValue() + " on " + latestMeasurementDate.getValue())
        );

        // 8. Evaluate Climate Risk Level
        ClimateRiskLevel climateRiskLevel = climateRiskEvaluator.evaluateClimateRisk(
                new NdviValue(consolidatedNdvi),
                weatherSnapshot,
                dynamicNutritionPolicy
        );

        // 9. Generate Mitigation Recommendations
        List<MitigationRecommendation> mitigationRecommendations = mitigationRecommendationGenerator.generateRecommendations(climateRiskLevel);


        // 10. Construct the MonitoringSummary with all new data
        MonitoringSummary monitoringSummary = new MonitoringSummary(
                userId,
                generalHealthStatus,
                new NdviValue(consolidatedNdvi),
                new AccumulatedChillHours(consolidatedChillHours),
                yieldForecast,
                latestMeasurementDate,
                weatherSnapshot,
                climateRiskLevel,
                mitigationRecommendations
        );

        return Optional.of(monitoringSummary);
    }

    private double estimatePlotYield(Plot plot, AgronomicStatistic statistic) {
        if (statistic == null) {
            return 0.0;
        }

        return yieldForecastEstimator.estimate(
                statistic.getNdviValue().getValue(),
                statistic.getChillPortions().getValue(),
                chillRequirementResolver.resolveFor(plot).value(),
                plot.getAreaSize().getHectares().doubleValue()
        ).getValue();
    }
}
