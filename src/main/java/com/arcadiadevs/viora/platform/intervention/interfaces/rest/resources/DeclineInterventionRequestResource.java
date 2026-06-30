package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource for declining an intervention request.
 */
public record DeclineInterventionRequestResource(
        String reason
) {
}
