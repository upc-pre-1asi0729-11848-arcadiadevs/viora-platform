package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateInterventionRequestCommand;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CreateInterventionRequestResource;

public class CreateInterventionRequestCommandFromResourceAssembler {

    public static CreateInterventionRequestCommand toCommandFromResource(CreateInterventionRequestResource resource) {
        return new CreateInterventionRequestCommand(
                resource.growerId(),
                resource.specialistId(),
                resource.alertId(),
                resource.reason(),
                resource.message()
        );
    }
}
