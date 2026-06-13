package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.AgronomicStatisticEntityFromAgronomicStatisticAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.AgronomicStatisticFromAgronomicStatisticEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataAgronomicStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the AgronomicStatisticRepository domain port.
 */
@Repository
@RequiredArgsConstructor
public class JpaAgronomicStatisticRepositoryAdapter implements AgronomicStatisticRepository {

    private final SpringDataAgronomicStatisticRepository springDataAgronomicStatisticRepository;

    @Override
    public List<AgronomicStatistic> findAllByUserIdAndMeasurementDateBetween(
            UserId userId,
            DateRange dateRange
    ) {
        return springDataAgronomicStatisticRepository
                .findAllByUserIdAndMeasurementDateBetween(
                        userId.getValue(),
                        dateRange.getStartDate(),
                        dateRange.getEndDate()
                )
                .stream()
                .map(AgronomicStatisticFromAgronomicStatisticEntityAssembler::toAggregateFromEntity)
                .toList();
    }

    @Override
    public List<AgronomicStatistic> findAllByUserIdAndPlotIdAndMeasurementDateBetween(
            UserId userId,
            PlotId plotId,
            DateRange dateRange
    ) {
        return springDataAgronomicStatisticRepository
                .findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                        userId.getValue(),
                        plotId.getValue(),
                        dateRange.getStartDate(),
                        dateRange.getEndDate()
                )
                .stream()
                .map(AgronomicStatisticFromAgronomicStatisticEntityAssembler::toAggregateFromEntity)
                .toList();
    }

    @Override
    public Optional<AgronomicStatistic> findByPlotIdAndMeasurementDate(
            PlotId plotId,
            MeasurementDate measurementDate
    ) {
        return springDataAgronomicStatisticRepository
                .findByPlotIdAndMeasurementDate(plotId.getValue(), measurementDate.getValue())
                .map(AgronomicStatisticFromAgronomicStatisticEntityAssembler::toAggregateFromEntity);
    }

    @Override
    public Optional<AgronomicStatistic> findLatestByPlotId(PlotId plotId) {
        return springDataAgronomicStatisticRepository
                .findTopByPlotIdOrderByMeasurementDateDesc(plotId.getValue())
                .map(AgronomicStatisticFromAgronomicStatisticEntityAssembler::toAggregateFromEntity);
    }

    @Override
    public AgronomicStatistic save(AgronomicStatistic agronomicStatistic) {
        var entity = AgronomicStatisticEntityFromAgronomicStatisticAssembler
                .toEntityFromAggregate(agronomicStatistic);

        var savedEntity = springDataAgronomicStatisticRepository.save(entity);

        return AgronomicStatisticFromAgronomicStatisticEntityAssembler
                .toAggregateFromEntity(savedEntity);
    }
}