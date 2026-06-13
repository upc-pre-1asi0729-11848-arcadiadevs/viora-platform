package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * REST projection for the per-plot weather forecast screen.
 *
 * <p>
 * Exposes hourly readings (about five days, the provider's detailed window),
 * daily aggregates, the thermal anomaly, agronomic warnings with an overall
 * risk, and the source availability and freshness.
 * </p>
 */
public record PlotWeatherForecastResource(
        Long plotId,
        Long userId,
        String plotName,
        Instant generatedAt,
        List<HourlyForecastResource> hourly,
        List<DailyForecastResource> daily,
        Double thermalAnomalyCelsius,
        String overallRisk,
        List<WeatherWarningResource> warnings,
        DataSourceResource source
) {

    public record HourlyForecastResource(
            Instant timestamp,
            Double temperatureCelsius,
            String weatherStatus,
            Integer humidityPercentage,
            Double precipitationMillimeters,
            Double windSpeedMetersPerSecond,
            Double windGustMetersPerSecond
    ) {
    }

    public record DailyForecastResource(
            LocalDate date,
            Double minTemperatureCelsius,
            Double maxTemperatureCelsius,
            Double averageTemperatureCelsius,
            String dominantStatus,
            Integer averageHumidityPercentage,
            Double totalPrecipitationMillimeters,
            Double maxWindGustMetersPerSecond
    ) {
    }

    public record WeatherWarningResource(
            String type,
            String severity,
            LocalDate date,
            String message
    ) {
    }

    public record DataSourceResource(
            String provider,
            String availability,
            Instant lastReadingAt,
            Integer updateFrequencyMinutes
    ) {
    }
}
