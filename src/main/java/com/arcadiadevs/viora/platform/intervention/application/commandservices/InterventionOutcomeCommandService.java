package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CloseInterventionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.ReportInterventionImpactCommand;

import java.util.Optional;

public interface InterventionOutcomeCommandService {
    Optional<InterventionOutcome> handle(ReportInterventionImpactCommand command);
    Optional<InterventionOutcome> handle(CloseInterventionCommand command);
}
