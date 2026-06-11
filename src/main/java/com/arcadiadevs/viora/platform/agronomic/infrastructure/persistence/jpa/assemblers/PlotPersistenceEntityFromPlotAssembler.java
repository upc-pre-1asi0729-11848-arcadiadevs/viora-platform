package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.PlotPersistenceEntity;

import java.util.Objects;

/**
 * Assembler to convert Plot domain aggregate into PlotPersistenceEntity.
 */
public class PlotPersistenceEntityFromPlotAssembler {

    private PlotPersistenceEntityFromPlotAssembler() {
    }

    /**
     * Converts a Plot aggregate into a PlotPersistenceEntity.
     *
     * @param plot The Plot aggregate.
     * @return The PlotPersistenceEntity.
     */
    public static PlotPersistenceEntity toEntityFromAggregate(Plot plot) {
        Objects.requireNonNull(plot, "Plot aggregate is required.");

        var entity = new PlotPersistenceEntity();
        if (plot.getId() != null) {
            entity.setId(plot.getId().getValue());
        }
        return updateEntityFromAggregate(plot, entity);
    }

    /**
     * Copies aggregate state into an existing persistence entity.
     *
     * @param plot The plot aggregate.
     * @param entity The persistence entity to update.
     * @return The updated persistence entity.
     */
    public static PlotPersistenceEntity updateEntityFromAggregate(
            Plot plot,
            PlotPersistenceEntity entity
    ) {
        if (plot == null) {
            throw new IllegalArgumentException("Plot aggregate is required.");
        }
        if (entity == null) {
            throw new IllegalArgumentException("Plot persistence entity is required.");
        }

        entity.setUserId(plot.getUserId().getValue());
        entity.setName(plot.getName().getValue());
        entity.setPolygonCoordinates(plot.getPolygonCoordinates());
        entity.setAreaSize(plot.getAreaSize().getHectares());
        entity.setCropType(plot.getCropType());
        entity.setVariety(plot.getVariety());
        entity.setLocation(plot.getLocation());
        entity.setCampaign(plot.getCampaign());
        entity.setNotes(plot.getNotes());
        entity.setActive(plot.getActive());

        return entity;
    }
}
