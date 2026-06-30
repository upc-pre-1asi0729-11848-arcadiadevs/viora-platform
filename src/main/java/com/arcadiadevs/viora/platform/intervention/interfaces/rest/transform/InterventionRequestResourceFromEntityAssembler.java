package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionRequestResource;

public class InterventionRequestResourceFromEntityAssembler {

    public static InterventionRequestResource toResourceFromEntity(InterventionRequest entity) {
        return new InterventionRequestResource(
                entity.getId() != null ? entity.getId().value() : null,
                entity.getReferenceCode() != null ? entity.getReferenceCode().code() : null,
                entity.getGrowerId(),
                entity.getSpecialistId(),
                entity.getAlertId(),
                entity.getReason(),
                entity.getMessage(),
                entity.getStatus() != null ? entity.getStatus().name() : null
        );
    }
}
