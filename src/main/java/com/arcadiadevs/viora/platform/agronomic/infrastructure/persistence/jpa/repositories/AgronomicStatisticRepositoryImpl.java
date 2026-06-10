package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository adapter that bridges the agronomic statistic domain repository port with Spring Data JPA.
 */
@Repository
public class AgronomicStatisticRepositoryImpl implements AgronomicStatisticRepository {

    private final SpringDataAgronomicStatisticRepository springDataAgronomicStatisticRepository;

    public AgronomicStatisticRepositoryImpl(SpringDataAgronomicStatisticRepository springDataAgronomicStatisticRepository) {
        this.springDataAgronomicStatisticRepository = springDataAgronomicStatisticRepository;
    }

    /**
     * Finds all agronomic statistics by user id and measurement date range.
     *
     * @param userId The user identifier.
     * @param dateRange The measurement date range.
     * @return A list of agronomic statistics.
     */
    @Override
    public List<AgronomicStatistic> findAllByUserIdAndMeasurementDateBetween(
            UserId userId,
            DateRange dateRange
    ) {
        return springDataAgronomicStatisticRepository.findAllByUserIdAndMeasurementDateBetween(
                userId.userId(),
                dateRange.startDate(),
                dateRange.endDate()
        );
    }

    /**
     * Finds all agronomic statistics by user id, plot id and measurement date range.
     *
     * @param userId The user identifier.
     * @param plotId The plot identifier.
     * @param dateRange The measurement date range.
     * @return A list of agronomic statistics.
     */
    @Override
    public List<AgronomicStatistic> findAllByUserIdAndPlotIdAndMeasurementDateBetween(
            UserId userId,
            PlotId plotId,
            DateRange dateRange
    ) {
        return springDataAgronomicStatisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                userId.userId(),
                plotId.plotId(),
                dateRange.startDate(),
                dateRange.endDate()
        );
    }

    /**
     * Saves an agronomic statistic.
     *
     * @param agronomicStatistic The agronomic statistic to save.
     * @return The saved agronomic statistic.
     */
    @Override
    public AgronomicStatistic save(AgronomicStatistic agronomicStatistic) {
        return springDataAgronomicStatisticRepository.save(agronomicStatistic);
    }
}