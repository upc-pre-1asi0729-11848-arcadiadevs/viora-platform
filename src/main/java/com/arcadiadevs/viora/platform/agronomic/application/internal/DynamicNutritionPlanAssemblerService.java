package com.arcadiadevs.viora.platform.agronomic.application.internal;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanUnavailableException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillSeasonEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.DynamicNutritionPlanGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PhenologicalRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Internal application service that assembles dynamic nutrition plans.
 *
 * <p>
 * Gathers provider-backed satellite NDVI and current weather for a plot,
 * evaluates climate risk and delegates plan generation rules to the
 * {@link DynamicNutritionPlanGenerator} domain service.
 * </p>
 */
@Service
public class DynamicNutritionPlanAssemblerService {

    private final AgroMonitoringImageryService imageryService;
    private final WeatherDataService weatherDataService;
    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final ClimateRiskEvaluator climateRiskEvaluator;
    private final PhenologicalRiskEvaluator phenologicalRiskEvaluator;
    private final ChillSeasonEvaluator chillSeasonEvaluator;
    private final ChillRequirementResolver chillRequirementResolver;
    private final DynamicNutritionPlanGenerator dynamicNutritionPlanGenerator;
    private final DynamicNutritionPolicy dynamicNutritionPolicy;
    private final Clock clock;

    public DynamicNutritionPlanAssemblerService(
            AgroMonitoringImageryService imageryService,
            WeatherDataService weatherDataService,
            AgronomicStatisticRepository agronomicStatisticRepository,
            ClimateRiskEvaluator climateRiskEvaluator,
            PhenologicalRiskEvaluator phenologicalRiskEvaluator,
            ChillSeasonEvaluator chillSeasonEvaluator,
            ChillRequirementResolver chillRequirementResolver,
            DynamicNutritionPlanGenerator dynamicNutritionPlanGenerator,
            DynamicNutritionPolicy dynamicNutritionPolicy,
            Clock clock
    ) {
        this.imageryService = imageryService;
        this.weatherDataService = weatherDataService;
        this.agronomicStatisticRepository = agronomicStatisticRepository;
        this.climateRiskEvaluator = climateRiskEvaluator;
        this.phenologicalRiskEvaluator = phenologicalRiskEvaluator;
        this.chillSeasonEvaluator = chillSeasonEvaluator;
        this.chillRequirementResolver = chillRequirementResolver;
        this.dynamicNutritionPlanGenerator = dynamicNutritionPlanGenerator;
        this.dynamicNutritionPolicy = dynamicNutritionPolicy;
        this.clock = clock;
    }

    /**
     * Assembles a dynamic nutrition plan for the given plot.
     *
     * @param plot The plot the plan is generated for.
     * @return The assembled active plan.
     * @throws DynamicNutritionPlanUnavailableException when the agronomic data
     *         required to generate the plan is missing or the climate risk does
     *         not justify a plan.
     */
    public DynamicNutritionPlan assembleForPlot(Plot plot) {
        var userId = plot.getUserId();
        var plotId = plot.getId();
        var generatedDate = LocalDate.now(clock);

        var imagery = imageryService.findCurrentImagery(plot)
                .filter(value -> value.ndviMean() != null)
                .orElseThrow(() -> new DynamicNutritionPlanUnavailableException(
                        "Current AgroMonitoring NDVI is not available for plot %d."
                                .formatted(plotId.getValue())
                ));

        var weatherSnapshot = weatherDataService.getCurrentWeatherSnapshot(plot)
                .orElseThrow(() -> new DynamicNutritionPlanUnavailableException(
                        "Current AgroMonitoring weather is not available for plot %d."
                                .formatted(plotId.getValue())));

        var ndviValue = new NdviValue(imagery.ndviMean());
        var climateRiskLevel = climateRiskEvaluator.evaluateClimateRisk(
                ndviValue,
                weatherSnapshot,
                dynamicNutritionPolicy
        );
        var phenologicalRisk = evaluatePhenologicalRisk(plot, weatherSnapshot, generatedDate);

        // A compensatory plan is justified by EITHER an immediate climate risk OR a
        // phenological (chill-deficit) risk; drive generation by the higher of the two.
        var triggeringRisk = higherSeverity(climateRiskLevel, phenologicalRisk);

        return dynamicNutritionPlanGenerator.generatePlan(
                userId,
                plotId,
                ndviValue,
                triggeringRisk,
                weatherSnapshot,
                generatedDate,
                toUtcDate(imagery.captureDate()),
                dynamicNutritionPolicy
        );
    }

    /* Phenological (chill-deficit) risk for the plot; chill-only here, no NDVI
     * trend (which never raises HIGH and so cannot change the trigger). */
    private ClimateRiskLevel evaluatePhenologicalRisk(
            Plot plot,
            WeatherSnapshot weatherSnapshot,
            LocalDate today
    ) {
        var chillPortions = agronomicStatisticRepository.findLatestByPlotId(plot.getId())
                .map(statistic -> statistic.getChillPortions().getValue())
                .orElse(null);
        var chillRequirement = chillRequirementResolver.resolveFor(plot).value();
        var temperatureAnomaly = weatherSnapshot.getTemperature()
                - dynamicNutritionPolicy.temperatureReferenceCelsius();
        var inChillRiskWindow = chillSeasonEvaluator.isInChillRiskWindow(
                plot.getPolygonCoordinates().centroid().getLatitude(), today);

        return phenologicalRiskEvaluator.evaluate(
                chillPortions, chillRequirement, temperatureAnomaly, false, inChillRiskWindow);
    }

    private ClimateRiskLevel higherSeverity(ClimateRiskLevel first, ClimateRiskLevel second) {
        return severity(first) >= severity(second) ? first : second;
    }

    private int severity(ClimateRiskLevel level) {
        return switch (level) {
            case EXTREME -> 4;
            case HIGH -> 3;
            case MODERATE -> 2;
            case LOW -> 1;
            case UNKNOWN -> 0;
        };
    }

    private LocalDate toUtcDate(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
