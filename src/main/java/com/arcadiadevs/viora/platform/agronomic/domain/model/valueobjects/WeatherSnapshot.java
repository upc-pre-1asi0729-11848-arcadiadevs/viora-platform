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
    private final Double temperature; // New field

    public WeatherSnapshot(
            WeatherStatus weatherStatus,
            MeasurementDate measurementDate,
            ClimateRiskLevel climateRiskLevel,
            Double temperature // New parameter
    ) {
        validateRequiredFields(weatherStatus, measurementDate, climateRiskLevel, temperature);
        validateConsistency(weatherStatus, climateRiskLevel); // Consistency rules might also involve temperature

        this.weatherStatus = weatherStatus;
        this.measurementDate = measurementDate;
        this.climateRiskLevel = climateRiskLevel;
        this.temperature = temperature; // Assign new field
    }

    private void validateRequiredFields(
            WeatherStatus weatherStatus,
            MeasurementDate measurementDate,
            ClimateRiskLevel climateRiskLevel,
            Double temperature // New parameter
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
        if (temperature == null) { // New validation
            throw new IllegalArgumentException("Temperature is required.");
        }
        // Add more specific temperature validations if needed (e.g., range)
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