package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to list the interventions of a grower — the accepted assistance cases and
 * their downstream technical-service lifecycle (prescription → execution → outcome).
 */
public record GetGrowerInterventionsQuery(Long growerId) {
    public GetGrowerInterventionsQuery {
        if (growerId == null || growerId <= 0) {
            throw new IllegalArgumentException("Grower ID must be a positive number.");
        }
    }
}
