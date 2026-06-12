package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicWeatherWarning;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherForecast;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherWarningType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherForecastAdvisorTest {

    private final WeatherForecastAdvisor advisor = new WeatherForecastAdvisor();

    @Test
    void aggregatesDailyForecastsAndRaisesAgronomicWarnings() {
        var forecast = new WeatherForecast(Instant.parse("2026-06-11T00:00:00Z"), List.of(
                // Day 1: frost, storm, high wind and heavy rain.
                reading("2026-06-11T00:00:00Z", 1.0, WeatherStatus.SUNNY, 50, 0.0, 0.5, 5.0, 3.0, 18.0),
                reading("2026-06-11T12:00:00Z", 4.0, WeatherStatus.STORMY, 60, 25.0, 3.0, 6.0, 5.0, 10.0),
                // Day 2: heat stress.
                reading("2026-06-12T10:00:00Z", 36.0, WeatherStatus.SUNNY, 30, 0.0, 20.0, 37.0, 2.0, 4.0)
        ));

        var analysis = advisor.analyze(forecast, 10.0);

        assertEquals(2, analysis.dailyForecasts().size());
        assertEquals(0.5, analysis.dailyForecasts().getFirst().minTemperatureCelsius(), 1e-9);
        assertEquals(ClimateRiskLevel.EXTREME, analysis.overallRisk());

        Set<WeatherWarningType> types = analysis.warnings().stream()
                .map(AgronomicWeatherWarning::type)
                .collect(Collectors.toSet());
        assertTrue(types.contains(WeatherWarningType.FROST));
        assertTrue(types.contains(WeatherWarningType.STORM));
        assertTrue(types.contains(WeatherWarningType.HIGH_WIND));
        assertTrue(types.contains(WeatherWarningType.HEAVY_RAIN));
        assertTrue(types.contains(WeatherWarningType.HEAT_STRESS));

        // Forecast mean (1, 4, 36) ≈ 13.7; anomaly vs baseline 10 ≈ 3.7.
        assertEquals(3.7, analysis.thermalAnomalyCelsius(), 1e-9);
    }

    @Test
    void calmForecastHasNoWarningsAndNullAnomalyWithoutBaseline() {
        var forecast = new WeatherForecast(Instant.parse("2026-06-11T00:00:00Z"), List.of(
                reading("2026-06-11T00:00:00Z", 18.0, WeatherStatus.CLOUDY, 55, 0.0, 16.0, 22.0, 3.0, 6.0),
                reading("2026-06-11T12:00:00Z", 21.0, WeatherStatus.SUNNY, 50, 0.0, 18.0, 24.0, 4.0, 7.0)
        ));

        var analysis = advisor.analyze(forecast, null);

        assertTrue(analysis.warnings().isEmpty());
        assertEquals(ClimateRiskLevel.LOW, analysis.overallRisk());
        assertNull(analysis.thermalAnomalyCelsius());
    }

    private WeatherReading reading(
            String timestamp,
            double temperature,
            WeatherStatus status,
            Integer humidity,
            Double precipitation,
            Double min,
            Double max,
            Double windSpeed,
            Double windGust
    ) {
        return new WeatherReading(
                Instant.parse(timestamp),
                temperature,
                status,
                humidity,
                precipitation,
                min,
                max,
                windSpeed,
                windGust
        );
    }
}
