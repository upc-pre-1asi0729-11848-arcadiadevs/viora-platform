package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClimateRiskEvaluatorTest {

    private final ClimateRiskEvaluator evaluator = new ClimateRiskEvaluator();
    private final DynamicNutritionPolicy policy =
            new DynamicNutritionPolicy(20.0, 0.30, 0.50, 3, 2, 2.5, 3.0, 1.2);

    @Test
    void preservesExtremeProviderRisk() {
        assertEquals(
                ClimateRiskLevel.EXTREME,
                evaluator.evaluateClimateRisk(
                        new NdviValue(0.70),
                        weather(ClimateRiskLevel.EXTREME),
                        policy
                )
        );
    }

    @Test
    void raisesHighRiskWhenVegetationFallsBelowConfiguredThreshold() {
        assertEquals(
                ClimateRiskLevel.HIGH,
                evaluator.evaluateClimateRisk(
                        new NdviValue(0.20),
                        weather(ClimateRiskLevel.LOW),
                        policy
                )
        );
    }

    @Test
    void raisesModerateRiskFromWeatherOrVegetationSignal() {
        assertEquals(
                ClimateRiskLevel.MODERATE,
                evaluator.evaluateClimateRisk(
                        new NdviValue(0.70),
                        weather(ClimateRiskLevel.MODERATE),
                        policy
                )
        );
        assertEquals(
                ClimateRiskLevel.MODERATE,
                evaluator.evaluateClimateRisk(
                        new NdviValue(0.40),
                        weather(ClimateRiskLevel.LOW),
                        policy
                )
        );
    }

    @Test
    void keepsHealthyVegetationAndLowWeatherAtLowRisk() {
        assertEquals(
                ClimateRiskLevel.LOW,
                evaluator.evaluateClimateRisk(
                        new NdviValue(0.70),
                        weather(ClimateRiskLevel.LOW),
                        policy
                )
        );
    }

    @Test
    void doesNotInventLowRiskWhenWeatherRiskIsUnknown() {
        assertEquals(
                ClimateRiskLevel.UNKNOWN,
                evaluator.evaluateClimateRisk(
                        new NdviValue(0.70),
                        weather(ClimateRiskLevel.UNKNOWN),
                        policy
                )
        );
    }

    private WeatherSnapshot weather(ClimateRiskLevel riskLevel) {
        return new WeatherSnapshot(
                WeatherStatus.SUNNY,
                new MeasurementDate(LocalDate.of(2026, 6, 12)),
                riskLevel,
                22.0
        );
    }
}
