package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource representing the intervention request metrics for a grower.
 */
public record GrowerRequestMetricsResource(
        ActiveAlertResource activeAlert,
        Integer availableSpecialists,
        String requestStatus
) {
}
