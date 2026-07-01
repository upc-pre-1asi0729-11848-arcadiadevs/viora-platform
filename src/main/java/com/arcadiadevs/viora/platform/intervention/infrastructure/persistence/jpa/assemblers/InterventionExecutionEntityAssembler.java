package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationDate;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.AppliedArea;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.InterventionExecutionEntity;

public class InterventionExecutionEntityAssembler {

    public static InterventionExecution toDomain(InterventionExecutionEntity entity) {
        if (entity == null) {
            return null;
        }

        var domain = new InterventionExecution(
                entity.getTreatmentPrescriptionId(),
                new ApplicationDate(entity.getApplicationDate()),
                new AppliedArea(entity.getAppliedArea()),
                entity.getExecutionStatus(),
                entity.getFieldNote()
        );
        domain.restoreIdentity(new InterventionExecutionId(entity.getId()));
        return domain;
    }

    public static InterventionExecutionEntity toEntity(InterventionExecution domain) {
        if (domain == null) {
            return null;
        }

        var entity = new InterventionExecutionEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setTreatmentPrescriptionId(domain.getTreatmentPrescriptionId());
        entity.setApplicationDate(domain.getApplicationDate().date());
        entity.setAppliedArea(domain.getAppliedArea().description());
        entity.setExecutionStatus(domain.getExecutionStatus());
        entity.setFieldNote(domain.getFieldNote());

        return entity;
    }
}
