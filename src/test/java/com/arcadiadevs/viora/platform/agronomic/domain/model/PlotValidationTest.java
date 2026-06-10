package com.arcadiadevs.viora.platform.agronomic.domain.model;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PlotValidationTest {

    @Test
    void rejectsEmptyPatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdatePlotCommand(1L, null, null, null, null, null)
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
}
