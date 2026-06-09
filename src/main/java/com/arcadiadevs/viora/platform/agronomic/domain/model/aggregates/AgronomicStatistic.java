package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.InvalidAgronomicMetricException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.RecordAgronomicStatisticCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicStatisticId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * AgronomicStatistic aggregate root.
 *
 * <p>
 * Represents an agronomic statistic registered for a plot.
 * It stores vegetation index data and climatic metrics used to analyze agronomic trends.
 * </p>
 */
@Getter
public class AgronomicStatistic extends AbstractDomainAggregateRoot<AgronomicStatistic> {

    /**
     * The unique identifier for the agronomic statistic.
     */
    @Setter
    private Long id;

    /**
     * The owner user identifier.
     */
    private UserId userId;

    /**
     * The plot identifier related to the agronomic statistic.
     */
    private PlotId plotId;

    /**
     * The measurement date of the agronomic statistic.
     */
    private MeasurementDate measurementDate;

    /**
     * The NDVI value. It must be between -1 and 1.
     */
    private NdviValue ndviValue;

    /**
     * The chill portions value. It cannot be negative.
     */
    private ChillPortions chillPortions;

    /**
     * The chill hours value. It cannot be negative.
     */
    private ChillHours chillHours;

    /**
     * Default constructor for AgronomicStatistic.
     */
    public AgronomicStatistic() {
    }

    /**
     * Constructor for AgronomicStatistic with value objects.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param measurementDate The measurement date.
     * @param ndviValue The NDVI value.
     * @param chillPortions The chill portions value.
     * @param chillHours The chill hours value.
     */
    public AgronomicStatistic(
            UserId userId,
            PlotId plotId,
            MeasurementDate measurementDate,
            NdviValue ndviValue,
            ChillPortions chillPortions,
            ChillHours chillHours
    ) {
        this.validateAgronomicStatisticData(userId, plotId, measurementDate, ndviValue, chillPortions, chillHours);

        this.userId = userId;
        this.plotId = plotId;
        this.measurementDate = measurementDate;
        this.ndviValue = ndviValue;
        this.chillPortions = chillPortions;
        this.chillHours = chillHours;
    }

    /**
     * Constructor for AgronomicStatistic with a RecordAgronomicStatisticCommand.
     *
     * @param command The RecordAgronomicStatisticCommand.
     */
    public AgronomicStatistic(RecordAgronomicStatisticCommand command) {
        this(
                command.userId(),
                command.plotId(),
                command.measurementDate(),
                command.ndviValue(),
                command.chillPortions(),
                command.chillHours()
        );
    }

    /**
     * Gets the agronomic statistic identifier as a value object.
     *
     * @return The AgronomicStatisticId.
     */
    public AgronomicStatisticId getAgronomicStatisticId() {
        return new AgronomicStatisticId(this.id);
    }

    /**
     * Updates the agronomic statistic metrics.
     *
     * @param measurementDate The new measurement date.
     * @param ndviValue The new NDVI value.
     * @param chillPortions The new chill portions value.
     * @param chillHours The new chill hours value.
     * @return The updated AgronomicStatistic instance.
     */
    public AgronomicStatistic updateMetrics(
            MeasurementDate measurementDate,
            NdviValue ndviValue,
            ChillPortions chillPortions,
            ChillHours chillHours
    ) {
        this.validateAgronomicMetrics(ndviValue, chillPortions, chillHours);

        this.measurementDate = measurementDate;
        this.ndviValue = ndviValue;
        this.chillPortions = chillPortions;
        this.chillHours = chillHours;

        return this;
    }

    /**
     * Validates all agronomic statistic data.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param measurementDate The measurement date.
     * @param ndviValue The NDVI value.
     * @param chillPortions The chill portions value.
     * @param chillHours The chill hours value.
     */
    private void validateAgronomicStatisticData(
            UserId userId,
            PlotId plotId,
            MeasurementDate measurementDate,
            NdviValue ndviValue,
            ChillPortions chillPortions,
            ChillHours chillHours
    ) {
        if (userId == null || plotId == null || measurementDate == null) {
            throw new InvalidAgronomicMetricException("Agronomic statistic data cannot be null");
        }

        this.validateAgronomicMetrics(ndviValue, chillPortions, chillHours);
    }

    /**
     * Validates agronomic metrics.
     *
     * @param ndviValue The NDVI value.
     * @param chillPortions The chill portions value.
     * @param chillHours The chill hours value.
     */
    private void validateAgronomicMetrics(
            NdviValue ndviValue,
            ChillPortions chillPortions,
            ChillHours chillHours
    ) {
        if (ndviValue == null || chillPortions == null || chillHours == null) {
            throw new InvalidAgronomicMetricException("Agronomic metrics cannot be null");
        }

        if (ndviValue.ndviValue().compareTo(BigDecimal.valueOf(-1)) < 0 ||
                ndviValue.ndviValue().compareTo(BigDecimal.ONE) > 0) {
            throw new InvalidAgronomicMetricException("NDVI value must be between -1 and 1");
        }

        if (chillPortions.chillPortions().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAgronomicMetricException("Chill portions cannot be negative");
        }

        if (chillHours.chillHours().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAgronomicMetricException("Chill hours cannot be negative");
        }
    }
}