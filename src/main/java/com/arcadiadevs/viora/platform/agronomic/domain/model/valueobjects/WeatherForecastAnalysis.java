package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.List;

/**
 * Agronomic analysis derived from a weather forecast.
 *
 * @param dailyForecasts Per-day aggregates ordered by date.
 * @param thermalAnomalyCelsius Forecast mean temperature minus the baseline, or null when no baseline.
 * @param overallRisk Highest climate risk implied by the warnings.
 * @param warnings Agronomic warnings for the forecast window.
 */
public record WeatherForecastAnalysis(
        List<DailyWeather> dailyForecasts,
        Double thermalAnomalyCelsius,
        ClimateRiskLevel overallRisk,
        List<AgronomicWeatherWarning> warnings
) {
    public WeatherForecastAnalysis {
        if (overallRisk == null) {
            throw new IllegalArgumentException("Overall risk is required.");
        }
        dailyForecasts = dailyForecasts == null ? List.of() : List.copyOf(dailyForecasts);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
