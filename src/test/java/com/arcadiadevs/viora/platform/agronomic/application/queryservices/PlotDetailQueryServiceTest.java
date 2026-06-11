package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotDetailMetadataProvider;
import com.arcadiadevs.viora.platform.agronomic.application.internal.outboundservices.AgroMonitoringImageryService;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IntegrationLinkStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotDetailQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.AreaSize;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PolygonCoordinates;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SatelliteImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlotDetailQueryServiceTest {

    @Test
    void buildsDetailFromPersistedMetadataAndIntegrations() {
        var plot = createPlot();
        var imagery = new SatelliteImagery(
                "image-1",
                "https://api.agromonitoring.com/tile/{z}/{x}/{y}",
                Instant.parse("2026-06-10T18:00:00Z"),
                0.68,
                2.5
        );
        var device = new IoTDevice(
                new PlotId(1L),
                new DeviceName("Soil moisture"),
                IoTDeviceStatus.ACTIVE
        );
        device.setId(7L);

        var plots = mock(PlotRepository.class);
        when(plots.findById(new PlotId(1L))).thenReturn(Optional.of(plot));
        var devices = mock(IoTDeviceRepository.class);
        when(devices.findAllByPlotId(1L)).thenReturn(List.of(device));
        var metadataProvider = (PlotDetailMetadataProvider) plotId -> Optional.of(
                new PlotDetailMetadataProvider.PlotMetadata(
                        Instant.parse("2026-05-01T15:00:00Z"),
                        Instant.parse("2026-05-03T12:00:00Z"),
                        new PlotDetailMetadataProvider.MonitoringIntegrationMetadata(
                                Instant.parse("2026-05-01T16:00:00Z"),
                                Instant.parse("2026-06-11T11:00:00Z"),
                                Instant.parse("2026-06-10T18:00:00Z")
                        ),
                        List.of(new PlotDetailMetadataProvider.DeviceMetadata(
                                7L,
                                Instant.parse("2026-05-04T10:00:00Z"),
                                Instant.parse("2026-06-11T10:30:00Z")
                        ))
                )
        );
        var service = new PlotDetailQueryService(
                plots,
                new StubImageryService(imagery, true),
                devices,
                metadataProvider
        );

        var result = service.handle(new GetPlotDetailQuery(10L, 1L));

        assertTrue(result.isSuccess());
        var detail = result.success().orElseThrow();
        assertEquals("VALIDATED", detail.boundaryStatus());
        assertEquals(IntegrationLinkStatus.ACTIVE, detail.climateMonitoring());
        assertEquals(IntegrationLinkStatus.ACTIVE, detail.satelliteNdvi());
        assertEquals(IntegrationLinkStatus.ACTIVE, detail.iotTelemetry());
        assertEquals(1, detail.onlineDeviceCount());
        assertEquals(
                Instant.parse("2026-06-11T11:00:00Z"),
                detail.satelliteLastSyncAt()
        );
        assertEquals(Instant.parse("2026-06-11T10:30:00Z"), detail.lastIotActivityAt());
        assertEquals(7L, detail.devices().getFirst().device().getId());
        assertEquals(
                "SATELLITE_MONITORING_SYNCHRONIZED",
                detail.recentConfigurationActivity().getFirst().type()
        );
    }

    @Test
    void rejectsDetailForDifferentOwner() {
        var plot = createPlot();
        var plots = mock(PlotRepository.class);
        when(plots.findById(new PlotId(1L))).thenReturn(Optional.of(plot));
        var devices = mock(IoTDeviceRepository.class);
        var service = new PlotDetailQueryService(
                plots,
                new StubImageryService(null, false),
                devices,
                plotId -> Optional.empty()
        );

        var result = service.handle(new GetPlotDetailQuery(99L, 1L));

        assertTrue(result.isFailure());
        assertEquals("PLOT_OWNERSHIP_FORBIDDEN", result.failure().orElseThrow().code());
    }

    private Plot createPlot() {
        var firstPoint = new GeoPoint(-12.0, -77.0);
        return new Plot(
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
        ).restoreIdentity(new PlotId(1L));
    }

    private record StubImageryService(
            SatelliteImagery imagery,
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
            return Optional.empty();
        }
    }
}
