package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.ServiceProposalQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.ServiceProposalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceProposalQueryServiceImpl implements ServiceProposalQueryService {

    private final ServiceProposalRepository serviceProposalRepository;

    public ServiceProposalQueryServiceImpl(ServiceProposalRepository serviceProposalRepository) {
        this.serviceProposalRepository = serviceProposalRepository;
    }

    @Override
    public List<ServiceProposal> findByInterventionRequestId(Long interventionRequestId) {
        return serviceProposalRepository.findByInterventionRequestId(interventionRequestId);
    }
}
