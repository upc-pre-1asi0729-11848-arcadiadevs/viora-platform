package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicStatisticId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillModelState;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.AgronomicStatisticEntity;

import java.util.Objects;

/**
 * Assembler to convert AgronomicStatisticEntity into AgronomicStatistic domain aggregate.
 */
public class AgronomicStatisticFromAgronomicStatisticEntityAssembler {

    private AgronomicStatisticFromAgronomicStatisticEntityAssembler() {
    }

    public static AgronomicStatistic toAggregateFromEntity(
            AgronomicStatisticEntity entity
    ) {
        Objects.requireNonNull(entity, "Agronomic statistic entity is required.");

        var chillModelState = new ChillModelState(
                entity.getChillIntermediateProduct() == null ? 0.0 : entity.getChillIntermediateProduct(),
                entity.getChillStateLastTemperature(),
                entity.getChillStatePriorTemperature()
        );

        var agronomicStatistic = new AgronomicStatistic(
                new UserId(entity.getUserId()),
                new PlotId(entity.getPlotId()),
                new MeasurementDate(entity.getMeasurementDate()),
                new NdviValue(entity.getNdviValue()),
                new ChillPortions(entity.getChillPortions()),
                new AccumulatedChillHours(entity.getChillHours()),
                chillModelState
        );

        agronomicStatistic.restoreIdentity(new AgronomicStatisticId(entity.getId()));

        return agronomicStatistic;
    }
}
