package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.PlotFromPlotPersistenceEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.PlotPersistenceEntityFromPlotAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.converters.PolygonCoordinatesAttributeConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PlotPersistenceMappingTest {

    @Test
    void preservesAggregateStateAcrossPersistenceMapping() {
        var plot = createPlot().deactivate();

        var entity = PlotPersistenceEntityFromPlotAssembler.toEntityFromAggregate(plot);
        var restoredPlot = PlotFromPlotPersistenceEntityAssembler.toAggregateFromEntity(entity);

        assertEquals(plot.getId(), restoredPlot.getId());
        assertEquals(plot.getUserId(), restoredPlot.getUserId());
        assertEquals(plot.getPolygonCoordinates(), restoredPlot.getPolygonCoordinates());
        assertEquals(plot.getAreaSize(), restoredPlot.getAreaSize());
        assertEquals("Tacna, Peru", restoredPlot.getLocation());
        assertEquals("2026 campaign", restoredPlot.getCampaign());
        assertEquals("Regular irrigation.", restoredPlot.getNotes());
        assertFalse(restoredPlot.isActive());
    }

    @Test
    void preservesPolygonAcrossDatabaseConversion() {
        var polygon = createPlot().getPolygonCoordinates();
        var converter = new PolygonCoordinatesAttributeConverter();

        var databaseValue = converter.convertToDatabaseColumn(polygon);
        var restoredPolygon = converter.convertToEntityAttribute(databaseValue);

        assertEquals(polygon, restoredPolygon);
    }

    @Test
    void recalculatesStalePersistedAreaFromPolygon() {
        var plot = createPlot();
        var entity = PlotPersistenceEntityFromPlotAssembler.toEntityFromAggregate(plot);
        entity.setAreaSize(java.math.BigDecimal.ONE);

        var restoredPlot = PlotFromPlotPersistenceEntityAssembler.toAggregateFromEntity(entity);

        assertEquals(
                plot.getPolygonCoordinates().estimatedAreaHectares(),
                restoredPlot.getAreaSize().getHectares()
        );
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        var polygon = new PolygonCoordinates(List.of(
                firstPoint,
                new GeoPoint(-12.0, -76.9),
                new GeoPoint(-12.1, -76.9),
                firstPoint
        ));
        var plot = new Plot(
                new UserId(10L),
                new PlotName("North field"),
                polygon,
                AreaSize.calculatedFrom(polygon),
                "Coffee",
                "Typica",
                "Tacna, Peru",
                "2026 campaign",
                "Regular irrigation."
        );
        return plot.restoreIdentity(new PlotId(1L));
    }
}
