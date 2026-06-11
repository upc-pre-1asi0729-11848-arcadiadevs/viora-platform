package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MitigationRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MonitoringSummaryId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherSnapshot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldForecast;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * MonitoringSummary aggregate root.
 *
 * <p>
 * Represents a summary of monitoring data for a specific user and date.
 * </p>
 */
@Getter
public class MonitoringSummary extends AbstractDomainAggregateRoot<MonitoringSummary> {

    private MonitoringSummaryId id;

    private UserId userId;

    private GeneralHealthStatus generalHealthStatus;

    private NdviValue ndviValue;

    private AccumulatedChillHours accumulatedChillHours;

    private YieldForecast yieldForecast;

    private MeasurementDate measurementDate;

    private WeatherSnapshot weatherSnapshot; // New field
    private ClimateRiskLevel climateRiskLevel; // New field
    private List<MitigationRecommendation> mitigationRecommendations; // New field

    /**
     * Default constructor.
     */
    protected MonitoringSummary() {
        this.mitigationRecommendations = Collections.emptyList(); // Initialize empty list
    }

    /**
     * Creates a monitoring summary.
     *
     * @param userId The owner user identifier.
     * @param generalHealthStatus The general health status.
     * @param ndviValue The NDVI value.
     * @param accumulatedChillHours The accumulated chill hours value.
     * @param yieldForecast The yield forecast value.
     * @param measurementDate The measurement date.
     * @param weatherSnapshot The weather snapshot.
     * @param climateRiskLevel The climate risk level.
     * @param mitigationRecommendations The list of mitigation recommendations.
     */
    public MonitoringSummary(
            UserId userId,
            GeneralHealthStatus generalHealthStatus,
            NdviValue ndviValue,
            AccumulatedChillHours accumulatedChillHours,
            YieldForecast yieldForecast,
            MeasurementDate measurementDate,
            WeatherSnapshot weatherSnapshot, // New parameter
            ClimateRiskLevel climateRiskLevel, // New parameter
            List<MitigationRecommendation> mitigationRecommendations // New parameter
    ) {
        validateRequiredFields(
                userId, generalHealthStatus, ndviValue, accumulatedChillHours, yieldForecast, measurementDate,
                weatherSnapshot, climateRiskLevel, mitigationRecommendations
        );
        validateNumericMetrics(ndviValue, accumulatedChillHours, yieldForecast);

        this.userId = userId;
        this.generalHealthStatus = generalHealthStatus;
        this.ndviValue = ndviValue;
        this.accumulatedChillHours = accumulatedChillHours;
        this.yieldForecast = yieldForecast;
        this.measurementDate = measurementDate;
        this.weatherSnapshot = weatherSnapshot; // Assign new field
        this.climateRiskLevel = climateRiskLevel; // Assign new field
        this.mitigationRecommendations = Objects.requireNonNullElse(mitigationRecommendations, Collections.emptyList()); // Assign new field
    }

    /**
     * Restores the identity assigned by persistence.
     *
     * @param id The persisted monitoring summary identifier.
     * @return The identified monitoring summary.
     */
    public MonitoringSummary restoreIdentity(MonitoringSummaryId id) {
        if (id == null) {
            throw new IllegalArgumentException("Monitoring summary ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Monitoring summary identity cannot be changed.");
        }

        this.id = id;
        return this;
    }

    private void validateRequiredFields(
            UserId userId,
            GeneralHealthStatus generalHealthStatus,
            NdviValue ndviValue,
            AccumulatedChillHours accumulatedChillHours,
            YieldForecast yieldForecast,
            MeasurementDate measurementDate,
            WeatherSnapshot weatherSnapshot, // New parameter
            ClimateRiskLevel climateRiskLevel, // New parameter
            List<MitigationRecommendation> mitigationRecommendations // New parameter
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (generalHealthStatus == null) {
            throw new IllegalArgumentException("General health status is required.");
        }
        if (ndviValue == null) {
            throw new IllegalArgumentException("NDVI value is required.");
        }
        if (accumulatedChillHours == null) {
            throw new IllegalArgumentException("Accumulated chill hours are required.");
        }
        if (yieldForecast == null) {
            throw new IllegalArgumentException("Yield forecast is required.");
        }
        if (measurementDate == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        if (weatherSnapshot == null) { // New validation
            throw new IllegalArgumentException("Weather snapshot is required.");
        }
        if (climateRiskLevel == null) { // New validation
            throw new IllegalArgumentException("Climate risk level is required.");
        }
        // Mitigation recommendations can be an empty list, so no null check needed for the list itself,
        // but the parameter should not be null. Handled by Objects.requireNonNullElse.
    }

    private void validateNumericMetrics(
            NdviValue ndviValue,
            AccumulatedChillHours accumulatedChillHours,
            YieldForecast yieldForecast
    ) {
        if (ndviValue.getValue() < 0) {
            throw new InvalidAgronomicMetricException("NDVI value cannot be negative.");
        }
        if (accumulatedChillHours.getValue() < 0) {
            throw new InvalidAgronomicMetricException("Accumulated chill hours cannot be negative.");
        }
        if (yieldForecast.getValue() < 0) {
            throw new InvalidAgronomicMetricException("Yield forecast cannot be negative.");
        }
    }
}