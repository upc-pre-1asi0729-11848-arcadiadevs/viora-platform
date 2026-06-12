package com.arcadiadevs.viora.platform.agronomic.domain.model;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlotValidationTest {

    @Test
    void rejectsEmptyPatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdatePlotCommand(1L, null, null, null, null)
        );
    }

    @Test
    void rejectsNonPositiveUserId() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(0L));
    }

    @Test
    void rejectsNonFiniteCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(Double.NaN, -77.0));
    }

    @Test
    void rejectsAreaScaleUnsupportedByPersistence() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaSize(new BigDecimal("1.234"))
        );
    }

    @Test
    void estimatesPolygonAreaInHectares() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        var polygon = new PolygonCoordinates(List.of(
                firstPoint,
                new GeoPoint(-12.0, -76.999),
                new GeoPoint(-12.001, -76.999),
                new GeoPoint(-12.001, -77.0),
                firstPoint
        ));

        assertTrue(polygon.estimatedAreaHectares().signum() > 0);
        assertEquals(2, polygon.estimatedAreaHectares().scale());
    }
}
