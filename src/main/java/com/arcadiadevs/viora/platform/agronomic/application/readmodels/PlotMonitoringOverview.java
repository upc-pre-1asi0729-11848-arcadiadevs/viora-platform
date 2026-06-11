package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;

import java.time.Instant;

/**
 * Per-plot monitoring projection for the My Plots overview screen.
 *
 * <p>
 * Combines the plot with its most recent monitoring signals: consolidated NDVI
 * (satellite first, recorded statistics as fallback), chill accumulation, the
 * derived health badge, connected device counts and the moment of the latest
 * data update. {@code activeAlertCount} is a placeholder (always zero) until
 * the alerts capability is implemented.
 * </p>
 *
 * @param plot The plot aggregate.
 * @param currentNdvi The most recent NDVI for the plot, or null when no data exists.
 * @param chillPortions The most recent chill portions reading, or null when no data exists.
 * @param healthStatus The health badge derived from the current NDVI.
 * @param onlineDeviceCount Number of IoT devices currently in ACTIVE state.
 * @param activeAlertCount Number of active alerts (placeholder, always 0 for now).
 * @param lastUpdatedAt Instant of the latest monitoring data, or null when no data exists.
 * @param climateMonitoring Current climate integration state.
 * @param satelliteNdvi Current satellite imagery integration state.
 */
public record PlotMonitoringOverview(
        Plot plot,
        Double currentNdvi,
        Double chillPortions,
        GeneralHealthStatus healthStatus,
        long onlineDeviceCount,
        int activeAlertCount,
        Instant lastUpdatedAt,
        IntegrationLinkStatus climateMonitoring,
        IntegrationLinkStatus satelliteNdvi
) {
}
