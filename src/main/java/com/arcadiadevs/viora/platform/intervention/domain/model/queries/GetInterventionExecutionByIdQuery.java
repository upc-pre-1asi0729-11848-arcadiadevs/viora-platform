package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

public record GetInterventionExecutionByIdQuery(Long interventionExecutionId) {
    public GetInterventionExecutionByIdQuery {
        if (interventionExecutionId == null || interventionExecutionId <= 0) {
            throw new IllegalArgumentException("Intervention execution ID must be provided and positive");
        }
    }
}
