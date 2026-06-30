package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.DeclineInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.DeclineInterventionRequestResource;

public class DeclineInterventionRequestCommandFromResourceAssembler {

    public static DeclineInterventionRequestCommand toCommandFromResource(Long interventionRequestId, DeclineInterventionRequestResource resource) {
        return new DeclineInterventionRequestCommand(interventionRequestId, resource.reason());
    }
}
