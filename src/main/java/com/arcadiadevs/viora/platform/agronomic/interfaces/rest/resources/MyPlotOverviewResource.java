package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Monitoring projection for one plot in the My Plots overview.
 */
public record MyPlotOverviewResource(
        Long id,
        Long userId,
        String name,
        String location,
        String campaign,
        String cropType,
        String variety,
        List<List<Double>> polygonCoordinates,
        BigDecimal areaSizeHectares,
        Double currentNdvi,
        Double chillPortions,
        String healthStatus,
        long onlineDeviceCount,
        int activeAlertCount,
        Instant lastUpdatedAt,
        String climateMonitoring,
        String satelliteNdvi
) {
}
