package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanUnavailableException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplicationWindow;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlanRationale;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Domain service for generating dynamic nutrition plans based on climate risk.
 *
 * <p>
 * Encapsulates the pure business rules that decide which nutrition inputs,
 * dosages and application window apply for a plot given its consolidated
 * NDVI, the evaluated climate risk and the current weather conditions.
 * </p>
 */
@Service
public class DynamicNutritionPlanGenerator {

    /**
     * Generates an active dynamic nutrition plan for the given plot condition.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param consolidatedNdvi The consolidated NDVI observed for the plot.
     * @param climateRiskLevel The evaluated climate risk level.
     * @param weatherSnapshot The current weather snapshot.
     * @param generatedDate The date the plan is generated on (provided by the caller).
     * @param ndviCaptureDate Date of the satellite observation.
     * @param policy Configured agronomic policy.
     * @return The generated active plan.
     * @throws DynamicNutritionPlanUnavailableException when the climate risk does not justify a plan.
     */
    public DynamicNutritionPlan generatePlan(
            UserId userId,
            PlotId plotId,
            NdviValue consolidatedNdvi,
            ClimateRiskLevel climateRiskLevel,
            WeatherSnapshot weatherSnapshot,
            LocalDate generatedDate,
            LocalDate ndviCaptureDate,
            DynamicNutritionPolicy policy
    ) {
        if (climateRiskLevel != ClimateRiskLevel.HIGH
                && climateRiskLevel != ClimateRiskLevel.EXTREME) {
            throw new DynamicNutritionPlanUnavailableException(
                    "The current climate risk level (%s) is not high enough to generate a dynamic nutrition plan."
                            .formatted(climateRiskLevel));
        }

        var inputRecommendations = buildInputRecommendations(climateRiskLevel, policy);
        var applicationWindow = buildApplicationWindow(climateRiskLevel, generatedDate, policy);
        var rationale = buildRationale(
                climateRiskLevel,
                consolidatedNdvi,
                weatherSnapshot,
                ndviCaptureDate,
                policy
        );

        return DynamicNutritionPlan.recommend(
                userId,
                plotId,
                inputRecommendations,
                applicationWindow,
                rationale,
                generatedDate
        );
    }

    private List<NutritionInputRecommendation> buildInputRecommendations(
            ClimateRiskLevel climateRiskLevel,
            DynamicNutritionPolicy policy
    ) {
        List<NutritionInputRecommendation> inputRecommendations = new ArrayList<>();

        inputRecommendations.add(new NutritionInputRecommendation(
                "Foliar nutrition support",
                "Improve stress response and recovery",
                policy.foliarSupportDosageLitersPerHectare(),
                "L/ha",
                NutritionInputStatus.RECOMMENDED
        ));

        if (climateRiskLevel == ClimateRiskLevel.HIGH || climateRiskLevel == ClimateRiskLevel.EXTREME) {
            inputRecommendations.add(new NutritionInputRecommendation(
                    "Potassium-calcium support",
                    "Support flowering consistency and reduce abortion risk",
                    policy.potassiumCalciumDosageKilogramsPerHectare(),
                    "kg/ha",
                    NutritionInputStatus.RECOMMENDED
            ));
        }

        inputRecommendations.add(new NutritionInputRecommendation(
                "Biostimulant support",
                "Reinforce vegetative recovery after stress periods",
                policy.biostimulantDosageLitersPerHectare(),
                "L/ha",
                NutritionInputStatus.OPTIONAL
        ));

        return inputRecommendations;
    }

    private NutritionApplicationWindow buildApplicationWindow(
            ClimateRiskLevel climateRiskLevel,
            LocalDate generatedDate,
            DynamicNutritionPolicy policy
    ) {
        var windowDays = switch (climateRiskLevel) {
            case EXTREME -> policy.extremeRiskWindowDays();
            case HIGH -> policy.highRiskWindowDays();
            default -> throw new IllegalArgumentException(
                    "Only HIGH or EXTREME risk can define a nutrition application window."
            );
        };

        return new NutritionApplicationWindow(generatedDate, generatedDate.plusDays(windowDays));
    }

    private PlanRationale buildRationale(
            ClimateRiskLevel climateRiskLevel,
            NdviValue consolidatedNdvi,
            WeatherSnapshot weatherSnapshot,
            LocalDate ndviCaptureDate,
            DynamicNutritionPolicy policy
    ) {
        var temperatureAnomaly = weatherSnapshot.getTemperature()
                - policy.temperatureReferenceCelsius();

        var summary = String.format(
                Locale.ROOT,
                "Plan generated from %s risk using AgroMonitoring NDVI %.2f captured on %s "
                        + "and weather observed on %s; temperature anomaly is %+.1f C "
                        + "against the configured agronomic reference.",
                climateRiskLevel.name(),
                consolidatedNdvi.getValue(),
                ndviCaptureDate,
                weatherSnapshot.getMeasurementDate().getValue(),
                temperatureAnomaly
        );

        return new PlanRationale(summary, climateRiskLevel, consolidatedNdvi, temperatureAnomaly);
    }
}
