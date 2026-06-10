package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MonitoringSummaryId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldForecast;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

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

    /**
     * Default constructor.
     */
    protected MonitoringSummary() {
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
     */
    public MonitoringSummary(
            UserId userId,
            GeneralHealthStatus generalHealthStatus,
            NdviValue ndviValue,
            AccumulatedChillHours accumulatedChillHours,
            YieldForecast yieldForecast,
            MeasurementDate measurementDate
    ) {
        validateRequiredFields(userId, generalHealthStatus, ndviValue, accumulatedChillHours, yieldForecast, measurementDate);
        validateNumericMetrics(ndviValue, accumulatedChillHours, yieldForecast);

        this.userId = userId;
        this.generalHealthStatus = generalHealthStatus;
        this.ndviValue = ndviValue;
        this.accumulatedChillHours = accumulatedChillHours;
        this.yieldForecast = yieldForecast;
        this.measurementDate = measurementDate;
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
            MeasurementDate measurementDate
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
    }

    private void validateNumericMetrics(
            NdviValue ndviValue,
            AccumulatedChillHours accumulatedChillHours,
            YieldForecast yieldForecast
    ) {
        // NdviValue and YieldForecast already have their own validation for negative values
        // in their constructors, so we just need to check AccumulatedChillHours here.
        // However, to be explicit and consistent with the task description,
        // we can add checks here if their internal validation was not sufficient.
        // For now, assuming their constructors handle the negative checks.
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