package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;

import java.math.BigDecimal;

/**
 * Read model returned after registering a plot.
 *
 * <p>
 * Enriches the persisted plot with the geodesic area estimated from its
 * boundary and the initial link state of each monitoring integration, so the
 * registration confirmation screen can be rendered from a single response.
 * </p>
 *
 * @param plot The persisted plot aggregate.
 * @param estimatedAreaHectares The area estimated from the polygon boundary.
 * @param climateMonitoring Link state of the zonal climate integration.
 * @param satelliteNdvi Link state of the satellite NDVI integration.
 * @param iotDevices Link state of the IoT telemetry integration.
 */
public record PlotRegistration(
        Plot plot,
        BigDecimal estimatedAreaHectares,
        IntegrationLinkStatus climateMonitoring,
        IntegrationLinkStatus satelliteNdvi,
        IntegrationLinkStatus iotDevices
) {
}
