package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidWeatherSnapshotException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a snapshot of weather conditions at a specific date.
 */
@Getter
@EqualsAndHashCode
public class WeatherSnapshot {

    private final WeatherStatus weatherStatus;
    private final MeasurementDate measurementDate;
    private final ClimateRiskLevel climateRiskLevel;

    public WeatherSnapshot(
            WeatherStatus weatherStatus,
            MeasurementDate measurementDate,
            ClimateRiskLevel climateRiskLevel
    ) {
        validateRequiredFields(weatherStatus, measurementDate, climateRiskLevel);
        validateConsistency(weatherStatus, climateRiskLevel);

        this.weatherStatus = weatherStatus;
        this.measurementDate = measurementDate;
        this.climateRiskLevel = climateRiskLevel;
    }

    private void validateRequiredFields(
            WeatherStatus weatherStatus,
            MeasurementDate measurementDate,
            ClimateRiskLevel climateRiskLevel
    ) {
        if (weatherStatus == null) {
            throw new IllegalArgumentException("Weather status is required.");
        }
        if (measurementDate == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        if (climateRiskLevel == null) {
            throw new IllegalArgumentException("Climate risk level is required.");
        }
    }

    private void validateConsistency(
            WeatherStatus weatherStatus,
            ClimateRiskLevel climateRiskLevel
    ) {
        // Example of a business invariant:
        // If the weather is stormy, the climate risk level cannot be LOW.
        if (weatherStatus == WeatherStatus.STORMY && climateRiskLevel == ClimateRiskLevel.LOW) {
            throw new InvalidWeatherSnapshotException("Inconsistent weather snapshot: Stormy weather cannot have a LOW climate risk level.");
        }
        // Add more consistency rules as needed.
    }
}