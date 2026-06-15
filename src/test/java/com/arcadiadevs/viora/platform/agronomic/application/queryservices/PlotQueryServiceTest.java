package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetMyPlotsOverviewQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotByIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotNdviTileQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotsWithCurrentImageryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ChillRequirementResolver;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PhenologicalRiskEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.PlotHealthEvaluator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AccumulatedChillHours;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillPortions;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviValue;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlotQueryServiceTest {

    private static final Clock TEST_CLOCK = Clock.fixed(
            Instant.parse("2026-06-11T12:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void getsActivePlotById() {
        var plot = createPlot();
        var service = createService(plot, new StubImageryService(null, null, false));

        var result = service.handle(new GetPlotByIdQuery(1L));

        assertTrue(result.isSuccess());
        assertEquals(new PlotId(1L), result.success().orElseThrow().getId());
    }

    @Test
    void hidesInactivePlotById() {
        var plot = createPlot().deactivate();
        var service = createService(plot, new StubImageryService(null, null, false));

        var result = service.handle(new GetPlotByIdQuery(1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_NOT_FOUND", result.failure().orElseThrow().code());
    }

    @Test
    void getsActivePlotsWithCurrentImagery() {
        var plot = createPlot();
        var imagery = new SatelliteImagery(
                "image-1",
                "https://api.agromonitoring.com/tile/{z}/{x}/{y}",
                Instant.parse("2026-05-02T00:00:00Z"),
                0.62,
                2.5
        );
        var service = createService(plot, new StubImageryService(imagery, null, true));

        var result = service.handle(new GetPlotsWithCurrentImageryQuery(10L));

        assertTrue(result.isSuccess());
        var readModel = result.success().orElseThrow().getFirst();
        assertEquals(plot, readModel.plot());
        assertEquals(imagery, readModel.currentImagery().orElseThrow());
    }

    @Test
    void streamsNdviTileForOwnedPlot() {
        var plot = createPlot();
        var tileBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G'};
        var service = createService(plot, new StubImageryService(null, tileBytes, true));

        var result = service.handle(new GetPlotNdviTileQuery(10L, 1L, 12, 1180, 2122));

        assertTrue(result.isSuccess());
        assertArrayEquals(tileBytes, result.success().orElseThrow());
    }

    @Test
    void returnsForbiddenTileWhenPlotIsNotOwnedByUser() {
        var plot = createPlot();
        var service = createService(
                plot,
                new StubImageryService(null, new byte[]{1}, true)
        );

        var result = service.handle(new GetPlotNdviTileQuery(99L, 1L, 12, 1180, 2122));

        assertTrue(result.isFailure());
        assertEquals("PLOT_OWNERSHIP_FORBIDDEN", result.failure().orElseThrow().code());
    }

    @Test
    void returnsNotFoundTileWhenNoImageryIsAvailable() {
        var plot = createPlot();
        var service = createService(plot, new StubImageryService(null, null, true));

        var result = service.handle(new GetPlotNdviTileQuery(10L, 1L, 12, 1180, 2122));

        assertTrue(result.isFailure());
        assertEquals("PLOT_IMAGERY_TILE_NOT_FOUND", result.failure().orElseThrow().code());
    }

    @Test
    void rejectsTileCoordinatesOutsideZoomRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GetPlotNdviTileQuery(10L, 1L, 2, 4, 0)
        );
    }

    @Test
    void buildsMyPlotsOverviewFromImageryStatisticsAndDevices() {
        var plot = createPlot();
        var imagery = new SatelliteImagery(
                "image-1",
                "https://api.agromonitoring.com/tile/{z}/{x}/{y}",
                Instant.parse("2026-06-10T18:00:00Z"),
                0.68,
                2.5
        );
        var statistic = new AgronomicStatistic(
                new UserId(10L),
                new PlotId(1L),
                new MeasurementDate(LocalDate.of(2026, 6, 11)),
                new NdviValue(0.55),
                new ChillPortions(72.0),
                new AccumulatedChillHours(110.0)
        );
        var activeDevice = new IoTDevice(
                new PlotId(1L),
                new DeviceName("Soil moisture"),
                IoTDeviceStatus.ACTIVE
        );
        var inactiveDevice = new IoTDevice(
                new PlotId(1L),
                new DeviceName("Temperature"),
                IoTDeviceStatus.INACTIVE
        );

        var statistics = mock(AgronomicStatisticRepository.class);
        when(statistics.findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                any(),
                any(),
                any()
        )).thenReturn(List.of(statistic));

        var devices = mock(IoTDeviceRepository.class);
        when(devices.findAllByPlotId(1L))
                .thenReturn(List.of(activeDevice, inactiveDevice));

        var service = new PlotQueryService(
                new QueryPlotRepository(plot),
                new StubImageryService(imagery, null, true),
                statistics,
                devices,
                TEST_CLOCK,
                new PlotHealthEvaluator(),
                new PhenologicalRiskEvaluator(),
                new ChillRequirementResolver(new ChillRequirementPolicy(50.0, Map.of("olive", 40.0)))
        );

        var result = service.handle(new GetMyPlotsOverviewQuery(10L));

        assertTrue(result.isSuccess());
        var overview = result.success().orElseThrow();
        assertEquals(1, overview.registeredPlotCount());
        assertEquals(new BigDecimal("12.50"), overview.monitoredAreaHectares());
        assertEquals(1, overview.climateLinkedPlotCount());
        assertEquals(1, overview.onlineDeviceCount());

        var plotOverview = overview.plots().getFirst();
        assertEquals(0.68, plotOverview.currentNdvi());
        assertEquals(72.0, plotOverview.chillPortions());
        assertEquals(GeneralHealthStatus.HEALTHY, plotOverview.healthStatus());
        assertEquals(
                com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel.LOW,
                plotOverview.phenologicalRisk()
        );
        assertEquals(1, plotOverview.onlineDeviceCount());
        assertEquals(0, plotOverview.activeAlertCount());
        assertEquals(Instant.parse("2026-06-11T00:00:00Z"), plotOverview.lastUpdatedAt());
        assertEquals(IntegrationLinkStatus.ACTIVE, plotOverview.climateMonitoring());
        assertEquals(IntegrationLinkStatus.ACTIVE, plotOverview.satelliteNdvi());
    }

    private PlotQueryService createService(
            Plot plot,
            StubImageryService imageryService
    ) {
        var statistics = mock(AgronomicStatisticRepository.class);
        when(statistics.findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                any(),
                any(),
                any()
        )).thenReturn(List.of());

        var devices = mock(IoTDeviceRepository.class);
        when(devices.findAllByPlotId(any())).thenReturn(List.of());

        return new PlotQueryService(
                new QueryPlotRepository(plot),
                imageryService,
                statistics,
                devices,
                TEST_CLOCK,
                new PlotHealthEvaluator(),
                new PhenologicalRiskEvaluator(),
                new ChillRequirementResolver(new ChillRequirementPolicy(50.0, Map.of("olive", 40.0)))
        );
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

    private record StubImageryService(
            SatelliteImagery imagery,
            byte[] tileBytes,
            boolean linked
    ) implements AgroMonitoringImageryService {

        @Override
        public boolean isIntegrationEnabled() {
            return true;
        }

        @Override
        public boolean isPlotLinked(Plot plot) {
            return linked;
        }

        @Override
        public Optional<SatelliteImagery> findCurrentImagery(Plot plot) {
            return Optional.ofNullable(imagery);
        }

        @Override
        public Optional<byte[]> fetchCurrentNdviTile(Plot plot, int zoom, int x, int y) {
            return Optional.ofNullable(tileBytes);
        }

        @Override
        public Optional<com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviHistory> findNdviHistory(
                Plot plot,
                com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange range
        ) {
            return Optional.empty();
        }

        @Override
        public com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata describeNdviSource(Plot plot) {
            return com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DataSourceMetadata.notConfigured("AgroMonitoring");
        }
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
