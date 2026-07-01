package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationDate;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.AppliedArea;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ExecutionStatus;

/**
 * Command to certify the application of an agrochemical prescription.
 *
 * @param treatmentPrescriptionId the ID of the prescription being certified
 * @param applicationDate         the date when the application occurred
 * @param appliedArea             the area where the application occurred
 * @param executionStatus         the status of the execution
 * @param fieldNote               optional note from the farmer
 */
public record CertifyApplicationCommand(
        Long treatmentPrescriptionId,
        ApplicationDate applicationDate,
        AppliedArea appliedArea,
        ExecutionStatus executionStatus,
        String fieldNote
) {
    public CertifyApplicationCommand {
        if (treatmentPrescriptionId == null || treatmentPrescriptionId <= 0) {
            throw new IllegalArgumentException("Treatment prescription ID must be provided and positive");
        }
        if (applicationDate == null) {
            throw new IllegalArgumentException("Application date must be provided");
        }
        if (appliedArea == null) {
            throw new IllegalArgumentException("Applied area must be provided");
        }
        if (executionStatus == null) {
            throw new IllegalArgumentException("Execution status must be provided");
        }
    }
}
