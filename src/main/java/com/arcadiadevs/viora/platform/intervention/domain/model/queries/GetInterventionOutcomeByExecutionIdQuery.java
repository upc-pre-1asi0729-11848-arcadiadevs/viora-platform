package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to get an intervention outcome by its associated execution ID.
 *
 * @param interventionExecutionId the intervention execution ID
 */
public record GetInterventionOutcomeByExecutionIdQuery(Long interventionExecutionId) {
    public GetInterventionOutcomeByExecutionIdQuery {
        if (interventionExecutionId == null || interventionExecutionId <= 0) {
            throw new IllegalArgumentException("Intervention execution ID must be provided and positive");
        }
    }
}
