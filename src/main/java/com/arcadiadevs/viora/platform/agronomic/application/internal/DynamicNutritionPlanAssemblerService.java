package com.arcadiadevs.viora.platform.agronomic.application.internal;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.WeatherDataService;
import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanUnavailableException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ClimateRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.DynamicNutritionPlanGenerator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
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
    private final ClimateRiskEvaluator climateRiskEvaluator;
    private final DynamicNutritionPlanGenerator dynamicNutritionPlanGenerator;
    private final DynamicNutritionPolicy dynamicNutritionPolicy;
    private final Clock clock;

    public DynamicNutritionPlanAssemblerService(
            AgroMonitoringImageryService imageryService,
            WeatherDataService weatherDataService,
            ClimateRiskEvaluator climateRiskEvaluator,
            DynamicNutritionPlanGenerator dynamicNutritionPlanGenerator,
            DynamicNutritionPolicy dynamicNutritionPolicy,
            Clock clock
    ) {
        this.imageryService = imageryService;
        this.weatherDataService = weatherDataService;
        this.climateRiskEvaluator = climateRiskEvaluator;
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

        return dynamicNutritionPlanGenerator.generatePlan(
                userId,
                plotId,
                ndviValue,
                climateRiskLevel,
                weatherSnapshot,
                generatedDate,
                toUtcDate(imagery.captureDate()),
                dynamicNutritionPolicy
        );
    }

    private LocalDate toUtcDate(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
