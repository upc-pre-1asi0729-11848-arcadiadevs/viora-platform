package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionOutcomeCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CloseInterventionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.ReportInterventionImpactCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionExecutionRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionOutcomeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InterventionOutcomeCommandServiceImpl implements InterventionOutcomeCommandService {

    private final InterventionOutcomeRepository interventionOutcomeRepository;
    private final InterventionExecutionRepository interventionExecutionRepository;

    public InterventionOutcomeCommandServiceImpl(InterventionOutcomeRepository interventionOutcomeRepository, InterventionExecutionRepository interventionExecutionRepository) {
        this.interventionOutcomeRepository = interventionOutcomeRepository;
        this.interventionExecutionRepository = interventionExecutionRepository;
    }

    @Override
    public Optional<InterventionOutcome> handle(ReportInterventionImpactCommand command) {
        var executionId = new InterventionExecutionId(command.interventionExecutionId());
        
        if (interventionExecutionRepository.findById(executionId.value()).isEmpty()) {
            throw new IllegalArgumentException("Intervention execution does not exist");
        }

        if (interventionOutcomeRepository.existsByInterventionExecutionId(executionId)) {
            throw new IllegalArgumentException("An outcome has already been reported for this execution");
        }

        var outcome = new InterventionOutcome(command);
        var savedOutcome = interventionOutcomeRepository.save(outcome);
        
        return Optional.of(savedOutcome);
    }

    @Override
    public Optional<InterventionOutcome> handle(CloseInterventionCommand command) {
        var outcomeOptional = interventionOutcomeRepository.findById(command.interventionOutcomeId());
        
        if (outcomeOptional.isEmpty()) {
            throw new IllegalArgumentException("Intervention outcome does not exist");
        }

        var outcome = outcomeOptional.get();
        outcome.close(command);
        var updatedOutcome = interventionOutcomeRepository.save(outcome);
        
        return Optional.of(updatedOutcome);
    }
}
