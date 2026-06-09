package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.PlotPersistenceEntity;

/**
 * Assembler to convert Plot domain aggregate into PlotPersistenceEntity.
 */
public class PlotPersistenceEntityFromPlotAssembler {

    /**
     * Converts a Plot aggregate into a PlotPersistenceEntity.
     *
     * @param plot The Plot aggregate.
     * @return The PlotPersistenceEntity.
     */
    public static PlotPersistenceEntity toEntityFromAggregate(Plot plot) {
        if (plot == null) return null;

        var entity = new PlotPersistenceEntity();

        entity.setId(plot.getId());
        entity.setUserId(plot.getUserId().getValue());
        entity.setName(plot.getName().getValue());
        entity.setPolygonCoordinates(plot.getPolygonCoordinates());
        entity.setAreaSize(plot.getAreaSize().getHectares());
        entity.setCropType(plot.getCropType());
        entity.setVariety(plot.getVariety());
        entity.setActive(plot.getActive());

        return entity;
    }
}