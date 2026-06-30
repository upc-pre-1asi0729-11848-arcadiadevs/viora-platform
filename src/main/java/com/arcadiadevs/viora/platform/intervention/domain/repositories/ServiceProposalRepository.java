package com.arcadiadevs.viora.platform.intervention.domain.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;

import java.util.List;
import java.util.Optional;

public interface ServiceProposalRepository {

    ServiceProposal save(ServiceProposal serviceProposal);

    Optional<ServiceProposal> findById(ServiceProposalId id);

    List<ServiceProposal> findByInterventionRequestId(Long interventionRequestId);
    
    List<ServiceProposal> findBySpecialistId(Long specialistId);
}
