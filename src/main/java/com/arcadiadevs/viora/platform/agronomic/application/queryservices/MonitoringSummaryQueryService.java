package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetCurrentMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.MitigationRecommendationGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.YieldForecastEstimator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
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

    public MonitoringSummaryQueryService(
            PlotRepository plotRepository,
            AgronomicStatisticRepository agronomicStatisticRepository,
            WeatherDataService weatherDataService,
            ClimateRiskEvaluator climateRiskEvaluator,
            MitigationRecommendationGenerator mitigationRecommendationGenerator,
            YieldForecastEstimator yieldForecastEstimator
    ) {
        this.plotRepository = plotRepository;
        this.agronomicStatisticRepository = agronomicStatisticRepository;
        this.weatherDataService = weatherDataService;
        this.climateRiskEvaluator = climateRiskEvaluator;
        this.mitigationRecommendationGenerator = mitigationRecommendationGenerator;
        this.yieldForecastEstimator = yieldForecastEstimator;
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

        // 4. Consolidate NDVI and Accumulated Chill Hours
        double consolidatedNdvi = allUserStatistics.stream()
                .mapToDouble(stat -> stat.getNdviValue().getValue())
                .average()
                .orElse(0.0); // Default if no NDVI values

        double consolidatedChillHours = allUserStatistics.stream()
                .mapToDouble(stat -> stat.getChillHours().getValue())
                .average()
                .orElse(0.0); // Default if no chill hours

        double consolidatedChillPortions = allUserStatistics.stream()
                .mapToDouble(stat -> stat.getChillPortions().getValue())
                .average()
                .orElse(0.0);

        double totalAreaHectares = userPlots.stream()
                .map(plot -> plot.getAreaSize().getHectares().doubleValue())
                .reduce(0.0, Double::sum);

        // 5. Determine GeneralHealthStatus and estimate yield (transparent heuristic)
        GeneralHealthStatus generalHealthStatus = determineGeneralHealthStatus(consolidatedNdvi);
        YieldForecast yieldForecast = yieldForecastEstimator.estimate(
                consolidatedNdvi, consolidatedChillPortions, totalAreaHectares);

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
                new AccumulatedChillHours(consolidatedChillHours),
                weatherSnapshot
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

    // Placeholder method for determining GeneralHealthStatus
    private GeneralHealthStatus determineGeneralHealthStatus(double ndvi) {
        if (ndvi < 0.3) {
            return GeneralHealthStatus.CRITICAL;
        } else if (ndvi < 0.6) {
            return GeneralHealthStatus.WARNING;
        } else {
            return GeneralHealthStatus.HEALTHY;
        }
    }

}
