package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsWithCurrentImageryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlotQueryServiceTest {

    @Test
    void getsActivePlotById() {
        var plot = createPlot();
        var service = new PlotQueryService(
                new QueryPlotRepository(plot),
                ignored -> Optional.empty()
        );

        var result = service.handle(new GetPlotByIdQuery(1L));

        assertTrue(result.isSuccess());
        assertEquals(new PlotId(1L), result.success().orElseThrow().getId());
    }

    @Test
    void hidesInactivePlotById() {
        var plot = createPlot().deactivate();
        var service = new PlotQueryService(
                new QueryPlotRepository(plot),
                ignored -> Optional.empty()
        );

        var result = service.handle(new GetPlotByIdQuery(1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_NOT_FOUND", result.failure().orElseThrow().code());
    }

    @Test
    void getsActivePlotsWithCurrentImagery() {
        var plot = createPlot();
        var imagery = new SatelliteImagery(
                "image-1",
                "https://api.agromonitoring.com/tile/{z}/{x}/{y}?appid=test",
                Instant.parse("2026-05-02T00:00:00Z"),
                0.62,
                2.5
        );
        var service = new PlotQueryService(
                new QueryPlotRepository(plot),
                ignored -> Optional.of(imagery)
        );

        var result = service.handle(new GetPlotsWithCurrentImageryQuery(10L));

        assertTrue(result.isSuccess());
        var readModel = result.success().orElseThrow().getFirst();
        assertEquals(plot, readModel.plot());
        assertEquals(imagery, readModel.currentImagery().orElseThrow());
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        var plot = new Plot(
                new UserId(10L),
                new PlotName("North field"),
                new PolygonCoordinates(List.of(
                        firstPoint,
                        new GeoPoint(-12.0, -76.9),
                        new GeoPoint(-12.1, -76.9),
                        firstPoint
                )),
                new AreaSize(new BigDecimal("12.50")),
                "Coffee",
                "Typica"
        );
        return plot.restoreIdentity(new PlotId(1L));
    }

    private record QueryPlotRepository(Plot plot) implements PlotRepository {

        @Override
        public Optional<Plot> findById(PlotId id) {
            return plot.getId().equals(id) ? Optional.of(plot) : Optional.empty();
        }

        @Override
        public List<Plot> findAll() {
            return List.of(plot);
        }

        @Override
        public List<Plot> findByUserId(UserId userId) {
            return plot.belongsTo(userId) && plot.isActive() ? List.of(plot) : List.of();
        }

        @Override
        public Optional<Plot> findByNameAndUserId(PlotName name, UserId userId) {
            return Optional.empty();
        }

        @Override
        public Plot save(Plot plot) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsById(PlotId id) {
            return findById(id).isPresent();
        }

        @Override
        public boolean existsByNameAndUserId(PlotName name, UserId userId) {
            return false;
        }

        @Override
        public boolean existsByNameAndUserIdAndIdIsNot(
                PlotName name,
                UserId userId,
                PlotId id
        ) {
            return false;
        }

        @Override
        public boolean hasRelatedOperationalRecords(PlotId id) {
            return false;
        }

        @Override
        public void deleteById(PlotId id) {
            throw new UnsupportedOperationException();
        }
    }
}
