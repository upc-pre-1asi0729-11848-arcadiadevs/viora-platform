package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to get an intervention request by its ID.
 */
public record GetInterventionRequestByIdQuery(Long interventionRequestId) {
    public GetInterventionRequestByIdQuery {
        if (interventionRequestId == null || interventionRequestId <= 0) {
            throw new IllegalArgumentException("Intervention request ID must be a positive number.");
        }
    }
}
