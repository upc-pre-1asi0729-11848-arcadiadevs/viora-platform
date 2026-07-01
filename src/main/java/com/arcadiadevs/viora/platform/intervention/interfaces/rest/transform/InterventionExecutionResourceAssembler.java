package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CertifyApplicationCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationDate;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.AppliedArea;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ExecutionStatus;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CertifyApplicationResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionExecutionResource;

public class InterventionExecutionResourceAssembler {

    public static CertifyApplicationCommand toCommandFromResource(CertifyApplicationResource resource) {
        return new CertifyApplicationCommand(
                resource.treatmentPrescriptionId(),
                new ApplicationDate(resource.applicationDate()),
                new AppliedArea(resource.appliedArea()),
                ExecutionStatus.valueOf(resource.executionStatus()),
                resource.fieldNote()
        );
    }

    public static InterventionExecutionResource toResourceFromDomain(InterventionExecution domain) {
        return new InterventionExecutionResource(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getTreatmentPrescriptionId().value(),
                domain.getApplicationDate().date(),
                domain.getAppliedArea().description(),
                domain.getExecutionStatus() != null ? domain.getExecutionStatus().name() : null,
                domain.getFieldNote()
        );
    }
}
