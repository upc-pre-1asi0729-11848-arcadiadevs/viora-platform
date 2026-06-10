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

    AgronomicStatistic save(AgronomicStatistic agronomicStatistic);
}