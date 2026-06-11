package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * REST projection for the My Plots detail screen.
 */
public record PlotDetailResource(
        Long id,
        Long userId,
        String name,
        String location,
        String campaign,
        String cropType,
        String variety,
        String notes,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSizeHectares,
        int boundaryPointCount,
        String boundaryStatus,
        Instant registeredAt,
        Instant lastConfigurationUpdateAt,
        MonitoringLinksResource monitoringLinks,
        IoTSummaryResource iot,
        List<IoTDeviceDetailResource> devices,
        List<ConfigurationActivityResource> recentConfigurationActivity
) {

    public record MonitoringLinksResource(
            String climateMonitoring,
            String satelliteNdvi,
            Instant climateLastSyncAt,
            Instant satelliteLastSyncAt
    ) {
    }

    public record IoTSummaryResource(
            String status,
            int linkedDeviceCount,
            long onlineDeviceCount,
            Instant lastActivityAt
    ) {
    }

    public record IoTDeviceDetailResource(
            Long id,
            String name,
            String status,
            Instant linkedAt,
            Instant lastActivityAt
    ) {
    }

    public record ConfigurationActivityResource(
            String type,
            String description,
            Instant occurredAt
    ) {
    }
}
