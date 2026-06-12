package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;

/**
 * Read model returned after registering a plot.
 *
 * <p>
 * Enriches the persisted plot with the initial link state of each monitoring
 * integration, so the
 * registration confirmation screen can be rendered from a single response.
 * </p>
 *
 * @param plot The persisted plot aggregate.
 * @param climateMonitoring Link state of the zonal climate integration.
 * @param satelliteNdvi Link state of the satellite NDVI integration.
 * @param iotDevices Link state of the IoT telemetry integration.
 */
public record PlotRegistration(
        Plot plot,
        IntegrationLinkStatus climateMonitoring,
        IntegrationLinkStatus satelliteNdvi,
        IntegrationLinkStatus iotDevices
) {
}
