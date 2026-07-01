package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionExecutionByIdQuery;

import java.util.Optional;

public interface InterventionExecutionQueryService {
    Optional<InterventionExecution> handle(GetInterventionExecutionByIdQuery query);
}
