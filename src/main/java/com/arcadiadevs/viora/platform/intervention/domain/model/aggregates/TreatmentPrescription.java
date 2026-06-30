package com.arcadiadevs.viora.platform.intervention.domain.model.aggregates;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.LogFieldInspectionDataCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.PrescribeTreatmentCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.AgrochemicalPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.FieldInspectionRecord;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionStatus;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import lombok.Getter;

/**
 * Aggregate Root for the Treatment Prescription phase.
 * Manages the field inspection and issuance of an agrochemical prescription.
 */
@Getter
public class TreatmentPrescription extends AuditableAbstractAggregateRoot<TreatmentPrescription> {

    private ServiceProposalId serviceProposalId;
    private FieldInspectionRecord fieldInspectionRecord;
    private AgrochemicalPrescription agrochemicalPrescription;
    private TreatmentPrescriptionStatus status;

    protected TreatmentPrescription() {
        // Required by JPA/Frameworks
    }

    /**
     * Creates a new TreatmentPrescription linked to a service proposal.
     *
     * @param serviceProposalId the ID of the accepted service proposal
     */
    public TreatmentPrescription(Long serviceProposalId) {
        if (serviceProposalId == null || serviceProposalId <= 0) {
            throw new IllegalArgumentException("Service proposal ID must be provided and positive");
        }
        this.serviceProposalId = new ServiceProposalId(serviceProposalId);
        this.status = TreatmentPrescriptionStatus.PENDING_INSPECTION;
    }

    /**
     * Logs the field inspection data.
     *
     * @param command the command containing inspection data
     */
    public void logFieldInspection(LogFieldInspectionDataCommand command) {
        if (this.status != TreatmentPrescriptionStatus.PENDING_INSPECTION) {
            throw new IllegalStateException("Field inspection can only be logged when pending");
        }
        this.fieldInspectionRecord = new FieldInspectionRecord(
                command.findingType(),
                command.incidenceLevel(),
                command.technicalDescription(),
                command.recordDate()
        );
        this.status = TreatmentPrescriptionStatus.INSPECTED;
    }

    /**
     * Issues the agrochemical prescription based on the inspection.
     *
     * @param command the command containing prescription data
     */
    public void prescribeTreatment(PrescribeTreatmentCommand command) {
        if (this.status != TreatmentPrescriptionStatus.INSPECTED) {
            throw new IllegalStateException("Treatment can only be prescribed after an inspection is logged");
        }
        this.agrochemicalPrescription = new AgrochemicalPrescription(
                command.applicationMethod(),
                command.sprayVolume(),
                command.preHarvestIntervalDays(),
                command.agronomistRecommendations(),
                command.requiredPPE(),
                command.products()
        );
        this.status = TreatmentPrescriptionStatus.PRESCRIBED;
    }

    public void restoreIdentity(TreatmentPrescriptionId id) {
        this.id = id;
    }

    public void restoreStatus(TreatmentPrescriptionStatus status) {
        this.status = status;
    }

    public void restoreFieldInspection(FieldInspectionRecord record) {
        this.fieldInspectionRecord = record;
    }

    public void restoreAgrochemicalPrescription(AgrochemicalPrescription prescription) {
        this.agrochemicalPrescription = prescription;
    }
}
