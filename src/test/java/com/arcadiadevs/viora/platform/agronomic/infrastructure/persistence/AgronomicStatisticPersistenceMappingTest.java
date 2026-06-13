package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AgronomicStatisticId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillModelState;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.AgronomicStatisticEntityFromAgronomicStatisticAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.AgronomicStatisticFromAgronomicStatisticEntityAssembler;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgronomicStatisticPersistenceMappingTest {

    @Test
    void preservesCompleteDynamicModelStateAcrossPersistenceMapping() {
        var modelState = new ChillModelState(1.0125, 12.0, 1.0);
        var statistic = new AgronomicStatistic(
                new UserId(10L),
                new PlotId(1L),
                new MeasurementDate(LocalDate.of(2026, 6, 12)),
                new NdviValue(0.62),
                new ChillPortions(18.5),
                new AccumulatedChillHours(120.0),
                modelState
        ).restoreIdentity(new AgronomicStatisticId(7L));

        var entity = AgronomicStatisticEntityFromAgronomicStatisticAssembler
                .toEntityFromAggregate(statistic);
        var restored = AgronomicStatisticFromAgronomicStatisticEntityAssembler
                .toAggregateFromEntity(entity);

        assertEquals(1.0, entity.getChillStatePriorTemperature());
        assertEquals(modelState, restored.getChillModelState());
    }
}
