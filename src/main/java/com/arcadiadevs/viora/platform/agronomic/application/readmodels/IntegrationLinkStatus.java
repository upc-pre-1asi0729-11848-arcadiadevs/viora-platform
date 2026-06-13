package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

/**
 * Link state between a plot and one of its monitoring integrations
 * (climate data, satellite imagery, IoT telemetry).
 */
public enum IntegrationLinkStatus {

    /** The integration is connected and delivering data. */
    ACTIVE,

    /** The integration is connected but the provider has not delivered data yet. */
    INITIALIZING,

    /** The integration is not connected for this plot. */
    NOT_LINKED
}
