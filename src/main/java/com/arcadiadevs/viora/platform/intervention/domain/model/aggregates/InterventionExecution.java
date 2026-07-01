package com.arcadiadevs.viora.platform.intervention.domain.model.aggregates;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CertifyApplicationCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationDate;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.AppliedArea;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ExecutionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Aggregate Root representing the execution (certification) of a treatment prescription.
 */
@Getter
public class InterventionExecution extends AbstractDomainAggregateRoot<InterventionExecution> {
    
    private InterventionExecutionId id;
    private TreatmentPrescriptionId treatmentPrescriptionId;
    private ApplicationDate applicationDate;
    private AppliedArea appliedArea;
    private ExecutionStatus executionStatus;
    private String fieldNote;

    protected InterventionExecution() {
        // Required by JPA
    }

    /**
     * Constructs an InterventionExecution from a CertifyApplicationCommand.
     *
     * @param command the command containing execution details
     */
    public InterventionExecution(CertifyApplicationCommand command) {
        this.treatmentPrescriptionId = new TreatmentPrescriptionId(command.treatmentPrescriptionId());
        this.applicationDate = command.applicationDate();
        this.appliedArea = command.appliedArea();
        this.executionStatus = command.executionStatus();
        this.fieldNote = command.fieldNote();
    }

    /**
     * Constructs an InterventionExecution for restoration from infrastructure.
     *
     * @param treatmentPrescriptionId the treatment prescription ID
     * @param applicationDate         the application date
     * @param appliedArea             the applied area
     * @param executionStatus         the execution status
     * @param fieldNote               the field note
     */
    public InterventionExecution(TreatmentPrescriptionId treatmentPrescriptionId, ApplicationDate applicationDate, AppliedArea appliedArea, ExecutionStatus executionStatus, String fieldNote) {
        this.treatmentPrescriptionId = treatmentPrescriptionId;
        this.applicationDate = applicationDate;
        this.appliedArea = appliedArea;
        this.executionStatus = executionStatus;
        this.fieldNote = fieldNote;
    }

    public void restoreIdentity(InterventionExecutionId id) {
        this.id = id;
    }
}
