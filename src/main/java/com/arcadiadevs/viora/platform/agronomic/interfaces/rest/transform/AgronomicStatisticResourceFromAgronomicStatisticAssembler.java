package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.AgronomicStatisticResource;

import java.util.Objects;

/**
 * Assembler to convert AgronomicStatistic aggregate into AgronomicStatisticResource.
 */
public final class AgronomicStatisticResourceFromAgronomicStatisticAssembler {

    private AgronomicStatisticResourceFromAgronomicStatisticAssembler() {
    }

    public static AgronomicStatisticResource toResourceFromAggregate(
            AgronomicStatistic agronomicStatistic
    ) {
        Objects.requireNonNull(agronomicStatistic, "Agronomic statistic aggregate is required.");

        return new AgronomicStatisticResource(
                agronomicStatistic.getMeasurementDate().getValue(),
                agronomicStatistic.getNdviValue().getValue(),
                agronomicStatistic.getChillPortions().getValue(),
                agronomicStatistic.getChillHours().getValue()
        );
    }
}