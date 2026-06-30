package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to get a Treatment Prescription by its ID.
 *
 * @param treatmentPrescriptionId the ID
 */
public record GetTreatmentPrescriptionByIdQuery(Long treatmentPrescriptionId) {
    public GetTreatmentPrescriptionByIdQuery {
        if (treatmentPrescriptionId == null || treatmentPrescriptionId <= 0) {
            throw new IllegalArgumentException("Treatment prescription ID must be provided and positive");
        }
    }
}
