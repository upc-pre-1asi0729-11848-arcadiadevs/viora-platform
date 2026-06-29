package com.arcadiadevs.viora.platform.intervention.domain.services;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetSpecialistCandidatesByAlertIdQuery;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistCandidateResource;
import java.util.List;

/**
 * Service to handle queries related to specialist candidates.
 */
public interface SpecialistCandidatesQueryService {
    
    /**
     * Handles the query to get specialist candidates for a specific alert.
     *
     * @param query the query containing the alert ID
     * @return a list of specialist candidate resources
     */
    List<SpecialistCandidateResource> handle(GetSpecialistCandidatesByAlertIdQuery query);
}
