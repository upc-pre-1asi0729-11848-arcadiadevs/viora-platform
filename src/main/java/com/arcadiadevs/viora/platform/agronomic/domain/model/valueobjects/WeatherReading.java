package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.Instant;

/**
 * A single weather reading at a point in time.
 *
 * <p>
 * Shared by current-condition forecasts and historical observations. The
 * optional daily {@code minTemperatureCelsius} / {@code maxTemperatureCelsius}
 * are only populated by forecast sources that expose them; historical readings
 * leave them {@code null}. Wind speed and gust are in meters per second when
 * provided by the source.
 * </p>
 *
 * @param timestamp Instant the reading refers to.
 * @param temperatureCelsius Air temperature in degrees Celsius.
 * @param weatherStatus Mapped weather condition.
 * @param humidityPercentage Relative humidity percentage, or null when unknown.
 * @param precipitationMillimeters Precipitation volume in millimeters, or null when unknown.
 * @param minTemperatureCelsius Forecast minimum temperature, or null when not provided.
 * @param maxTemperatureCelsius Forecast maximum temperature, or null when not provided.
 * @param windSpeedMetersPerSecond Wind speed in m/s, or null when not provided.
 * @param windGustMetersPerSecond Wind gust in m/s, or null when not provided.
 */
public record WeatherReading(
        Instant timestamp,
        double temperatureCelsius,
        WeatherStatus weatherStatus,
        Integer humidityPercentage,
        Double precipitationMillimeters,
        Double minTemperatureCelsius,
        Double maxTemperatureCelsius,
        Double windSpeedMetersPerSecond,
        Double windGustMetersPerSecond
) {
    public WeatherReading {
        if (timestamp == null) {
            throw new IllegalArgumentException("Weather reading timestamp is required.");
        }
        if (!Double.isFinite(temperatureCelsius)) {
            throw new IllegalArgumentException("Temperature must be finite.");
        }
        if (weatherStatus == null) {
            throw new IllegalArgumentException("Weather status is required.");
        }
        if (humidityPercentage != null && (humidityPercentage < 0 || humidityPercentage > 100)) {
            throw new IllegalArgumentException("Humidity percentage must be between 0 and 100.");
        }
        if (precipitationMillimeters != null
                && (!Double.isFinite(precipitationMillimeters) || precipitationMillimeters < 0)) {
            throw new IllegalArgumentException("Precipitation must be a non-negative finite value.");
        }
        if (minTemperatureCelsius != null && !Double.isFinite(minTemperatureCelsius)) {
            throw new IllegalArgumentException("Minimum temperature must be finite.");
        }
        if (maxTemperatureCelsius != null && !Double.isFinite(maxTemperatureCelsius)) {
            throw new IllegalArgumentException("Maximum temperature must be finite.");
        }
        if (minTemperatureCelsius != null
                && maxTemperatureCelsius != null
                && minTemperatureCelsius > maxTemperatureCelsius) {
            throw new IllegalArgumentException(
                    "Minimum temperature cannot be greater than maximum temperature."
            );
        }
        if (windSpeedMetersPerSecond != null
                && (!Double.isFinite(windSpeedMetersPerSecond) || windSpeedMetersPerSecond < 0)) {
            throw new IllegalArgumentException("Wind speed must be a non-negative finite value.");
        }
        if (windGustMetersPerSecond != null
                && (!Double.isFinite(windGustMetersPerSecond) || windGustMetersPerSecond < 0)) {
            throw new IllegalArgumentException("Wind gust must be a non-negative finite value.");
        }
    }
}
