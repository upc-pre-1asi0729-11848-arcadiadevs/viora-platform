package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.AgronomicStatisticEntity;

import java.util.Objects;

/**
 * Assembler to convert AgronomicStatistic domain aggregate into AgronomicStatisticEntity.
 */
public class AgronomicStatisticEntityFromAgronomicStatisticAssembler {

    private AgronomicStatisticEntityFromAgronomicStatisticAssembler() {
    }

    public static AgronomicStatisticEntity toEntityFromAggregate(
            AgronomicStatistic agronomicStatistic
    ) {
        Objects.requireNonNull(agronomicStatistic, "Agronomic statistic aggregate is required.");

        var entity = new AgronomicStatisticEntity();

        if (agronomicStatistic.getId() != null) {
            entity.setId(agronomicStatistic.getId().getValue());
        }

        entity.setUserId(agronomicStatistic.getUserId().getValue());
        entity.setPlotId(agronomicStatistic.getPlotId().getValue());
        entity.setMeasurementDate(agronomicStatistic.getMeasurementDate().getValue());
        entity.setNdviValue(agronomicStatistic.getNdviValue().getValue());
        entity.setChillPortions(agronomicStatistic.getChillPortions().getValue());
        entity.setChillHours(agronomicStatistic.getChillHours().getValue());

        var chillModelState = agronomicStatistic.getChillModelState();
        if (chillModelState != null) {
            entity.setChillIntermediateProduct(chillModelState.intermediateProduct());
            entity.setChillStateLastTemperature(chillModelState.previousHourTemperatureCelsius());
        }

        return entity;
    }
}