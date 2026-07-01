package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CertifyApplicationCommand;

import java.util.Optional;

public interface InterventionExecutionCommandService {
    Optional<InterventionExecution> handle(CertifyApplicationCommand command);
}
