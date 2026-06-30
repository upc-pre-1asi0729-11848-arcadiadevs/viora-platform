package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource representing an intervention request response.
 */
public record InterventionRequestResource(
        Long id,
        String referenceCode,
        Long growerId,
        Long specialistId,
        Long alertId,
        String reason,
        String message,
        String status
) {
}
