package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to get an intervention outcome by its ID.
 *
 * @param interventionOutcomeId the intervention outcome ID
 */
public record GetInterventionOutcomeByIdQuery(Long interventionOutcomeId) {
    public GetInterventionOutcomeByIdQuery {
        if (interventionOutcomeId == null || interventionOutcomeId <= 0) {
            throw new IllegalArgumentException("Intervention outcome ID must be provided and positive");
        }
    }
}
