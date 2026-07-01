package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to list a grower's intervention requests, optionally scoped to a single plot.
 * When {@code plotId} is null the full history for the grower is returned.
 */
public record GetGrowerInterventionRequestsQuery(Long growerId, Long plotId) {
    public GetGrowerInterventionRequestsQuery {
        if (growerId == null || growerId <= 0) {
            throw new IllegalArgumentException("Grower ID must be a positive number.");
        }
        if (plotId != null && plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number when provided.");
        }
    }
}
