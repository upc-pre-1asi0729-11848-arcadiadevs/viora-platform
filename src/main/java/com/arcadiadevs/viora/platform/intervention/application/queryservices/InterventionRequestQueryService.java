package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionRequestsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionRequestByIdQuery;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle queries related to intervention requests.
 */
public interface InterventionRequestQueryService {

    Optional<InterventionRequest> handle(GetInterventionRequestByIdQuery query);

    List<InterventionRequest> handle(GetGrowerInterventionRequestsQuery query);
}
