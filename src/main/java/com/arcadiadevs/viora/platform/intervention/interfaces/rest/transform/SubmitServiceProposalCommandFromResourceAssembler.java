package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SubmitServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SubmitServiceProposalResource;

public class SubmitServiceProposalCommandFromResourceAssembler {

    public static SubmitServiceProposalCommand toCommandFromResource(SubmitServiceProposalResource resource) {
        return new SubmitServiceProposalCommand(
                resource.interventionRequestId(),
                resource.specialistId(),
                resource.proposedDate(),
                resource.amount(),
                resource.currency(),
                resource.proposalDetails()
        );
    }
}
