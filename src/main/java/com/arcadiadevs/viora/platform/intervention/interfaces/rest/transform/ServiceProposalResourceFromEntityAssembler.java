package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.ServiceProposalResource;

public class ServiceProposalResourceFromEntityAssembler {

    public static ServiceProposalResource toResourceFromEntity(ServiceProposal entity) {
        return new ServiceProposalResource(
                entity.getId() != null ? entity.getId().value() : null,
                entity.getInterventionRequestId() != null ? entity.getInterventionRequestId().value() : null,
                entity.getSpecialistId(),
                entity.getServiceTitle(),
                entity.getDurationLabel(),
                entity.getScope(),
                entity.getProposedDate(),
                entity.getCostEstimate() != null ? entity.getCostEstimate().amount() : null,
                entity.getCostEstimate() != null ? entity.getCostEstimate().currency() : null,
                entity.getProposalDetails(),
                entity.getStatus() != null ? entity.getStatus().name() : null
        );
    }
}
