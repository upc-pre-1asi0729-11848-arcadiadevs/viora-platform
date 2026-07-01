package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.InterventionRequestEntity;

import java.util.Date;

public class InterventionRequestEntityAssembler {

    public static InterventionRequestEntity toEntity(InterventionRequest domain) {
        var entity = new InterventionRequestEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setReferenceCode(domain.getReferenceCode());
        entity.setGrowerId(domain.getGrowerId());
        entity.setPlotId(domain.getPlotId());
        entity.setSpecialistId(domain.getSpecialistId());
        entity.setAlertId(domain.getAlertId());
        entity.setReason(domain.getReason());
        entity.setMessage(domain.getMessage());
        entity.setStatus(domain.getStatus());
        entity.setDeclineReason(domain.getDeclineReason());
        return entity;
    }

    public static InterventionRequest toDomain(InterventionRequestEntity entity) {
        var domain = new InterventionRequest(
                entity.getGrowerId(),
                entity.getPlotId(),
                entity.getSpecialistId(),
                entity.getAlertId(),
                entity.getReason(),
                entity.getMessage()
        );
        domain.restoreIdentity(new InterventionRequestId(entity.getId()));
        domain.restoreState(
                entity.getStatus(),
                entity.getDeclineReason(),
                entity.getCreatedAt() != null ? Date.from(entity.getCreatedAt()) : null,
                entity.getUpdatedAt() != null ? Date.from(entity.getUpdatedAt()) : null
        );
        return domain;
    }
}
