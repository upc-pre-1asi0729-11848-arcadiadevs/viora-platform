package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateTreatmentPrescriptionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.LogFieldInspectionDataCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.PrescribeTreatmentCommand;

import java.util.Optional;

/**
 * Command service for managing Treatment Prescription operations.
 */
public interface TreatmentPrescriptionCommandService {
    
    /**
     * Handles the creation of a new treatment prescription.
     * @param command the creation command
     * @return the created aggregate
     */
    Optional<TreatmentPrescription> handle(CreateTreatmentPrescriptionCommand command);

    /**
     * Handles logging the field inspection data.
     * @param command the log command
     * @return the updated aggregate
     */
    Optional<TreatmentPrescription> handle(LogFieldInspectionDataCommand command);

    /**
     * Handles prescribing the agrochemical treatment.
     * @param command the prescribe command
     * @return the updated aggregate
     */
    Optional<TreatmentPrescription> handle(PrescribeTreatmentCommand command);
}
