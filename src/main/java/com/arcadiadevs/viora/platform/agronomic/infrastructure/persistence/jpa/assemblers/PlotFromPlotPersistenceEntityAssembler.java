package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.PlotPersistenceEntity;

import java.util.Objects;

/**
 * Assembler to convert PlotPersistenceEntity into Plot domain aggregate.
 */
public class PlotFromPlotPersistenceEntityAssembler {

    private PlotFromPlotPersistenceEntityAssembler() {
    }

    /**
     * Converts a PlotPersistenceEntity into a Plot aggregate.
     * @param entity The PlotPersistenceEntity.
     * @return The Plot aggregate.
     */
    public static Plot toAggregateFromEntity(PlotPersistenceEntity entity) {
        Objects.requireNonNull(entity, "Plot persistence entity is required.");

        var plot = new Plot(
                new UserId(entity.getUserId()),
                new PlotName(entity.getName()),
                entity.getPolygonCoordinates(),
                new AreaSize(entity.getAreaSize()),
                entity.getCropType(),
                entity.getVariety()
        );

        plot.restoreIdentity(new PlotId(entity.getId()));

        if (Boolean.FALSE.equals(entity.getActive())) {
            plot.deactivate();
        }

        return plot;
    }
}
