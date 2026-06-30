package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.DeclineInterventionRequestCommand;
import java.util.Optional;

/**
 * Service to handle commands related to intervention requests.
 */
public interface InterventionRequestCommandService {
    
    Optional<InterventionRequest> handle(CreateInterventionRequestCommand command);
    
    Optional<InterventionRequest> handle(DeclineInterventionRequestCommand command);
}
