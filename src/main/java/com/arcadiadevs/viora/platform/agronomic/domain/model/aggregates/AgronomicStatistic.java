package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicStatisticId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillModelState;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * AgronomicStatistic aggregate root.
 *
 * <p>
 * Represents the consolidated agronomic measurements of a plot for a specific date.
 * It stores satellite and climate metrics such as NDVI, chill portions and chill hours.
 * </p>
 */
@Getter
public class AgronomicStatistic extends AbstractDomainAggregateRoot<AgronomicStatistic> {

    private AgronomicStatisticId id;

    private UserId userId;

    private PlotId plotId;

    private MeasurementDate measurementDate;

    private NdviValue ndviValue;

    private ChillPortions chillPortions;

    private AccumulatedChillHours chillHours;

    /**
     * Carry-over state of the Dynamic Model chill accumulation, so the next
     * snapshot continues the season's chill-portion accumulation seamlessly.
     */
    private ChillModelState chillModelState;

    /**
     * Default constructor.
     */
    protected AgronomicStatistic() {
    }

    /**
     * Creates an agronomic statistic with no chill carry-over state (the start of
     * a plot's accumulation).
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param measurementDate The measurement date.
     * @param ndviValue The NDVI value.
     * @param chillPortions The chill portions value.
     * @param chillHours The accumulated chill hours value.
     */
    public AgronomicStatistic(
            UserId userId,
            PlotId plotId,
            MeasurementDate measurementDate,
            NdviValue ndviValue,
            ChillPortions chillPortions,
            AccumulatedChillHours chillHours
    ) {
        this(userId, plotId, measurementDate, ndviValue, chillPortions, chillHours, ChillModelState.empty());
    }

    /**
     * Creates an agronomic statistic.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param measurementDate The measurement date.
     * @param ndviValue The NDVI value.
     * @param chillPortions The chill portions value.
     * @param chillHours The accumulated chill hours value.
     * @param chillModelState The Dynamic Model carry-over state.
     */
    public AgronomicStatistic(
            UserId userId,
            PlotId plotId,
            MeasurementDate measurementDate,
            NdviValue ndviValue,
            ChillPortions chillPortions,
            AccumulatedChillHours chillHours,
            ChillModelState chillModelState
    ) {
        validateRequiredFields(userId, plotId, measurementDate, ndviValue, chillPortions, chillHours);

        this.userId = userId;
        this.plotId = plotId;
        this.measurementDate = measurementDate;
        this.ndviValue = ndviValue;
        this.chillPortions = chillPortions;
        this.chillHours = chillHours;
        this.chillModelState = chillModelState == null ? ChillModelState.empty() : chillModelState;
    }

    /**
     * Restores the identity assigned by persistence.
     *
     * @param id The persisted agronomic statistic identifier.
     * @return The identified agronomic statistic.
     */
    public AgronomicStatistic restoreIdentity(AgronomicStatisticId id) {
        if (id == null) {
            throw new IllegalArgumentException("Agronomic statistic ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Agronomic statistic identity cannot be changed.");
        }

        this.id = id;
        return this;
    }

    private void validateRequiredFields(
            UserId userId,
            PlotId plotId,
            MeasurementDate measurementDate,
            NdviValue ndviValue,
            ChillPortions chillPortions,
            AccumulatedChillHours chillHours
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (plotId == null) {
            throw new IllegalArgumentException("Plot ID is required.");
        }
        if (measurementDate == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        if (ndviValue == null) {
            throw new IllegalArgumentException("NDVI value is required.");
        }
        if (chillPortions == null) {
            throw new IllegalArgumentException("Chill portions are required.");
        }
        if (chillHours == null) {
            throw new IllegalArgumentException("Chill hours are required.");
        }
    }
}