package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response for the My Plots overview screen.
 */
public record MyPlotsOverviewResource(
        int registeredPlotCount,
        BigDecimal monitoredAreaHectares,
        int climateLinkedPlotCount,
        long onlineDeviceCount,
        List<MyPlotOverviewResource> plots
) {
}
