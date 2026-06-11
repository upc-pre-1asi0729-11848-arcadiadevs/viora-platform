package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated projection for the My Plots overview screen.
 *
 * <p>
 * Carries the summary cards (registered plots, monitored area, plots linked to
 * climate monitoring, online devices) together with the per-plot monitoring
 * rows.
 * </p>
 *
 * @param registeredPlotCount Total active plots owned by the user.
 * @param monitoredAreaHectares Sum of the productive area of all plots.
 * @param climateLinkedPlotCount Plots receiving zonal climate/satellite data.
 * @param onlineDeviceCount Total IoT devices currently in ACTIVE state.
 * @param plots The per-plot monitoring rows.
 */
public record MyPlotsOverview(
        int registeredPlotCount,
        BigDecimal monitoredAreaHectares,
        int climateLinkedPlotCount,
        long onlineDeviceCount,
        List<PlotMonitoringOverview> plots
) {
}
