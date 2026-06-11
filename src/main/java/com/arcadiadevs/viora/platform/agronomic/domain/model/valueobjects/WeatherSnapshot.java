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
    private final Double temperature;

    public WeatherSnapshot(
            WeatherStatus weatherStatus,
            MeasurementDate measurementDate,
            ClimateRiskLevel climateRiskLevel,
            Double temperature
    ) {
        validateRequiredFields(weatherStatus, measurementDate, climateRiskLevel, temperature);
        validateConsistency(weatherStatus, climateRiskLevel);

        this.weatherStatus = weatherStatus;
        this.measurementDate = measurementDate;
        this.climateRiskLevel = climateRiskLevel;
        this.temperature = temperature;
    }

    private void validateRequiredFields(
            WeatherStatus weatherStatus,
            MeasurementDate measurementDate,
            ClimateRiskLevel climateRiskLevel,
            Double temperature
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
        if (temperature == null) {
            throw new IllegalArgumentException("Temperature is required.");
        }
        if (!Double.isFinite(temperature)) {
            throw new IllegalArgumentException("Temperature must be finite.");
        }
    }

    private void validateConsistency(
            WeatherStatus weatherStatus,
            ClimateRiskLevel climateRiskLevel
    ) {
        if (weatherStatus == WeatherStatus.STORMY && climateRiskLevel == ClimateRiskLevel.LOW) {
            throw new InvalidWeatherSnapshotException("Inconsistent weather snapshot: Stormy weather cannot have a LOW climate risk level.");
        }
    }
}
