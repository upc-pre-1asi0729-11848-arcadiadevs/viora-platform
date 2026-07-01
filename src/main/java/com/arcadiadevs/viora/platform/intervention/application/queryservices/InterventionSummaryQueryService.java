package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionsQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionSummaryResource;

import java.util.List;

/**
 * Query service that composes a grower's interventions across the marketplace
 * lifecycle aggregates.
 */
public interface InterventionSummaryQueryService {

    List<InterventionSummaryResource> handle(GetGrowerInterventionsQuery query);
}
