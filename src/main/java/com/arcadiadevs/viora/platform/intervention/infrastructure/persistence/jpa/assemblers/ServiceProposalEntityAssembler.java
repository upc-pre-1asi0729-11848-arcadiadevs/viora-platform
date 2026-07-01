package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.ServiceProposalEntity;

public class ServiceProposalEntityAssembler {

    public static ServiceProposalEntity toEntity(ServiceProposal domain) {
        var entity = new ServiceProposalEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setInterventionRequestId(domain.getInterventionRequestId());
        entity.setSpecialistId(domain.getSpecialistId());
        entity.setServiceTitle(domain.getServiceTitle());
        entity.setDurationLabel(domain.getDurationLabel());
        entity.setScope(domain.getScope() != null ? java.util.List.copyOf(domain.getScope()) : java.util.List.of());
        entity.setProposedDate(domain.getProposedDate());
        if (domain.getCostEstimate() != null) {
            entity.setCostEstimate(domain.getCostEstimate());
        }
        entity.setProposalDetails(domain.getProposalDetails());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    public static ServiceProposal toDomain(ServiceProposalEntity entity) {
        var domain = new ServiceProposal(
                entity.getInterventionRequestId(),
                entity.getSpecialistId(),
                entity.getProposedDate(),
                entity.getCostEstimate() != null ? entity.getCostEstimate().amount() : null,
                entity.getCostEstimate() != null ? entity.getCostEstimate().currency() : null,
                entity.getProposalDetails()
        );
        domain.restoreIdentity(new ServiceProposalId(entity.getId()));
        domain.restoreStatus(entity.getStatus());
        domain.restoreDetails(
                entity.getServiceTitle(),
                entity.getDurationLabel(),
                entity.getScope() != null ? java.util.List.copyOf(entity.getScope()) : java.util.List.of()
        );
        return domain;
    }
}
