package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;

import java.util.List;

/**
 * Read operations for the Service Proposal aggregate.
 */
public interface ServiceProposalQueryService {

    /**
     * Lists the proposals submitted for an intervention request (newest lifecycle first).
     *
     * @param interventionRequestId the request whose proposals are retrieved
     * @return the proposals for the request
     */
    List<ServiceProposal> findByInterventionRequestId(Long interventionRequestId);
}
