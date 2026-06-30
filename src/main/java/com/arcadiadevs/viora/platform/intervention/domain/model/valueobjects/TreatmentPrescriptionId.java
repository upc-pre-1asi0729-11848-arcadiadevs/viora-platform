package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Value object representing the unique identifier of a Treatment Prescription.
 *
 * @param value the numeric ID
 */
public record TreatmentPrescriptionId(Long value) {
    public TreatmentPrescriptionId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Treatment prescription ID must be provided and positive");
        }
    }
}
