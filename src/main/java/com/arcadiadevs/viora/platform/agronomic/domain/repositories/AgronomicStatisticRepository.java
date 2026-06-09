package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;

import java.util.List;

/**
 * Agronomic statistic repository port.
 *
 * <p>
 * This repository defines the operations required to access agronomic statistics
 * from the domain layer.
 * </p>
 */
public interface AgronomicStatisticRepository {

    /**
     * Finds all agronomic statistics by user id and measurement date range.
     *
     * @param userId The user identifier.
     * @param dateRange The measurement date range.
     * @return A list of agronomic statistics.
     */
    List<AgronomicStatistic> findAllByUserIdAndMeasurementDateBetween(
            UserId userId,
            DateRange dateRange
    );

    /**
     * Finds all agronomic statistics by user id, plot id and measurement date range.
     *
     * @param userId The user identifier.
     * @param plotId The plot identifier.
     * @param dateRange The measurement date range.
     * @return A list of agronomic statistics.
     */
    List<AgronomicStatistic> findAllByUserIdAndPlotIdAndMeasurementDateBetween(
            UserId userId,
            PlotId plotId,
            DateRange dateRange
    );

    /**
     * Saves an agronomic statistic.
     *
     * @param agronomicStatistic The agronomic statistic to save.
     * @return The saved agronomic statistic.
     */
    AgronomicStatistic save(AgronomicStatistic agronomicStatistic);
}