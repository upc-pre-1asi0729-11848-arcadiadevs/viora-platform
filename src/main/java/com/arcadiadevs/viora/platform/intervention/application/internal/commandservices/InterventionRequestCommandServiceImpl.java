package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.exceptions.InterventionRequestNotFoundException;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.DeclineInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionRequestCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InterventionRequestCommandServiceImpl implements InterventionRequestCommandService {

    private final InterventionRequestRepository interventionRequestRepository;

    public InterventionRequestCommandServiceImpl(InterventionRequestRepository interventionRequestRepository) {
        this.interventionRequestRepository = interventionRequestRepository;
    }

    @Override
    @Transactional
    public Optional<InterventionRequest> handle(CreateInterventionRequestCommand command) {
        var interventionRequest = new InterventionRequest(command);
        var savedDomain = interventionRequestRepository.save(interventionRequest);
        return Optional.of(savedDomain);
    }

    @Override
    @Transactional
    public Optional<InterventionRequest> handle(DeclineInterventionRequestCommand command) {
        var domainOptional = interventionRequestRepository.findById(new InterventionRequestId(command.interventionRequestId()));
        if (domainOptional.isEmpty()) {
            throw new InterventionRequestNotFoundException(command.interventionRequestId());
        }
        
        var domain = domainOptional.get();
        domain.decline(command.reason());
        
        var savedDomain = interventionRequestRepository.save(domain);
        return Optional.of(savedDomain);
    }
}
