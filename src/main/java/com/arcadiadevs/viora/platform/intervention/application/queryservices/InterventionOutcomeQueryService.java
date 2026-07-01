package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionOutcomeByExecutionIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionOutcomeByIdQuery;

import java.util.Optional;

/**
 * Service interface for handling intervention outcome queries.
 */
public interface InterventionOutcomeQueryService {
    /**
     * Handles the get intervention outcome by ID query.
     *
     * @param query the get intervention outcome by ID query
     * @return an optional containing the intervention outcome if found
     */
    Optional<InterventionOutcome> handle(GetInterventionOutcomeByIdQuery query);

    /**
     * Handles the get intervention outcome by execution ID query.
     *
     * @param query the get intervention outcome by execution ID query
     * @return an optional containing the intervention outcome if found
     */
    Optional<InterventionOutcome> handle(GetInterventionOutcomeByExecutionIdQuery query);
}
