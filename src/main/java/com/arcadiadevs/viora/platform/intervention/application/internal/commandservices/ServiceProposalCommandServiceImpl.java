package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.ServiceProposalCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.AcceptServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.RejectServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SubmitServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionRequestRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.ServiceProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link ServiceProposalCommandService}.
 * Manages the write operations for the Service Proposal aggregate and keeps the
 * linked intervention request's status in sync with the proposal lifecycle.
 */
@Service
public class ServiceProposalCommandServiceImpl implements ServiceProposalCommandService {

    private final ServiceProposalRepository serviceProposalRepository;
    private final InterventionRequestRepository interventionRequestRepository;

    public ServiceProposalCommandServiceImpl(
            ServiceProposalRepository serviceProposalRepository,
            InterventionRequestRepository interventionRequestRepository) {
        this.serviceProposalRepository = serviceProposalRepository;
        this.interventionRequestRepository = interventionRequestRepository;
    }

    @Override
    @Transactional
    public Optional<ServiceProposal> handle(SubmitServiceProposalCommand command) {
        var serviceProposal = new ServiceProposal(command);
        var savedDomain = serviceProposalRepository.save(serviceProposal);

        // A submitted proposal moves the linked request to PROPOSAL_RECEIVED.
        interventionRequestRepository.findById(savedDomain.getInterventionRequestId())
                .ifPresent(request -> {
                    request.markProposalReceived();
                    interventionRequestRepository.save(request);
                });

        return Optional.of(savedDomain);
    }

    @Override
    @Transactional
    public Optional<ServiceProposal> handle(AcceptServiceProposalCommand command) {
        var domainOptional = serviceProposalRepository.findById(new ServiceProposalId(command.serviceProposalId()));
        if (domainOptional.isEmpty()) {
            return Optional.empty();
        }

        var domain = domainOptional.get();
        domain.accept();
        var savedDomain = serviceProposalRepository.save(domain);

        interventionRequestRepository.findById(savedDomain.getInterventionRequestId())
                .ifPresent(request -> {
                    request.accept();
                    interventionRequestRepository.save(request);
                });

        return Optional.of(savedDomain);
    }

    @Override
    @Transactional
    public Optional<ServiceProposal> handle(RejectServiceProposalCommand command) {
        var domainOptional = serviceProposalRepository.findById(new ServiceProposalId(command.serviceProposalId()));
        if (domainOptional.isEmpty()) {
            return Optional.empty();
        }

        var domain = domainOptional.get();
        domain.reject();
        var savedDomain = serviceProposalRepository.save(domain);

        // Rejecting the proposal reactivates the specialist search for the request.
        interventionRequestRepository.findById(savedDomain.getInterventionRequestId())
                .ifPresent(request -> {
                    request.decline("Proposal declined by grower");
                    interventionRequestRepository.save(request);
                });

        return Optional.of(savedDomain);
    }
}
