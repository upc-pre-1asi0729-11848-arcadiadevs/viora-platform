package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Agronomic statistic repository port.
 *
 * <p>
 * Defines the persistence operations required by the agronomic domain
 * to retrieve and record agronomic statistics.
 * </p>
 */
public interface AgronomicStatisticRepository {

    List<AgronomicStatistic> findAllByUserIdAndMeasurementDateBetween(
            UserId userId,
            DateRange dateRange
    );

    List<AgronomicStatistic> findAllByUserIdAndPlotIdAndMeasurementDateBetween(
            UserId userId,
            PlotId plotId,
            DateRange dateRange
    );

    /**
     * Finds the snapshot for a plot on a specific date, used to keep daily
     * ingestion idempotent (one snapshot per plot per day).
     */
    Optional<AgronomicStatistic> findByPlotIdAndMeasurementDate(
            PlotId plotId,
            MeasurementDate measurementDate
    );

    /**
     * Finds the most recent snapshot for a plot, used as the accumulation base
     * for the next daily chill snapshot.
     */
    Optional<AgronomicStatistic> findLatestByPlotId(PlotId plotId);

    AgronomicStatistic save(AgronomicStatistic agronomicStatistic);
}