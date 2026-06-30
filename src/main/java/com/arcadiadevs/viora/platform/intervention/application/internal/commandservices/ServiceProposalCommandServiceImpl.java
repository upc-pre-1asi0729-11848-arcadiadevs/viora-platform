package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.ServiceProposalCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.AcceptServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.RejectServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SubmitServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.ServiceProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link ServiceProposalCommandService}.
 * Manages the write operations for the Service Proposal aggregate.
 */
@Service
public class ServiceProposalCommandServiceImpl implements ServiceProposalCommandService {

    private final ServiceProposalRepository serviceProposalRepository;

    /**
     * Constructs a new ServiceProposalCommandServiceImpl.
     *
     * @param serviceProposalRepository the repository for service proposals
     */
    public ServiceProposalCommandServiceImpl(ServiceProposalRepository serviceProposalRepository) {
        this.serviceProposalRepository = serviceProposalRepository;
    }

    @Override
    @Transactional
    public Optional<ServiceProposal> handle(SubmitServiceProposalCommand command) {
        var serviceProposal = new ServiceProposal(command);
        var savedDomain = serviceProposalRepository.save(serviceProposal);
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
        return Optional.of(savedDomain);
    }
}
