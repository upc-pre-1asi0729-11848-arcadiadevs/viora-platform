package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Aggregated weather for a single forecast day.
 *
 * @param date Calendar day (UTC).
 * @param minTemperatureCelsius Minimum temperature for the day.
 * @param maxTemperatureCelsius Maximum temperature for the day.
 * @param averageTemperatureCelsius Mean temperature for the day.
 * @param dominantStatus Most frequent weather condition during the day.
 * @param averageHumidityPercentage Mean relative humidity, or null when unknown.
 * @param totalPrecipitationMillimeters Total precipitation for the day.
 * @param maxWindGustMetersPerSecond Strongest wind gust, or null when unknown.
 */
public record DailyWeather(
        LocalDate date,
        double minTemperatureCelsius,
        double maxTemperatureCelsius,
        double averageTemperatureCelsius,
        WeatherStatus dominantStatus,
        Integer averageHumidityPercentage,
        double totalPrecipitationMillimeters,
        Double maxWindGustMetersPerSecond
) {
    public DailyWeather {
        if (date == null) {
            throw new IllegalArgumentException("Daily weather date is required.");
        }
        if (!Double.isFinite(minTemperatureCelsius)
                || !Double.isFinite(maxTemperatureCelsius)
                || !Double.isFinite(averageTemperatureCelsius)) {
            throw new IllegalArgumentException("Daily temperatures must be finite.");
        }
        if (minTemperatureCelsius > maxTemperatureCelsius) {
            throw new IllegalArgumentException("Minimum temperature cannot exceed maximum temperature.");
        }
        if (dominantStatus == null) {
            throw new IllegalArgumentException("Dominant weather status is required.");
        }
        if (averageHumidityPercentage != null
                && (averageHumidityPercentage < 0 || averageHumidityPercentage > 100)) {
            throw new IllegalArgumentException("Average humidity must be between 0 and 100.");
        }
        if (!Double.isFinite(totalPrecipitationMillimeters) || totalPrecipitationMillimeters < 0) {
            throw new IllegalArgumentException("Total precipitation must be a non-negative finite value.");
        }
        if (maxWindGustMetersPerSecond != null
                && (!Double.isFinite(maxWindGustMetersPerSecond) || maxWindGustMetersPerSecond < 0)) {
            throw new IllegalArgumentException("Maximum wind gust must be a non-negative finite value.");
        }
    }
}
