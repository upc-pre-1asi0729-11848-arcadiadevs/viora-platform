package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource for creating an intervention request.
 */
public record CreateInterventionRequestResource(
        Long growerId,
        Long specialistId,
        Long alertId,
        String reason,
        String message
) {
}
