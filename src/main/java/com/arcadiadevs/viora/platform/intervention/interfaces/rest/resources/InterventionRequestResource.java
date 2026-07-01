package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import java.time.Instant;

/**
 * Resource representing an intervention request response.
 */
public record InterventionRequestResource(
        Long id,
        String referenceCode,
        Long growerId,
        Long plotId,
        Long specialistId,
        Long alertId,
        String reason,
        String message,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
