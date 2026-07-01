package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;

import java.util.Optional;

/**
 * Demo aid: there is no specialist-facing application yet, so this service issues
 * a technical prescription on the specialist's behalf for an accepted assistance
 * case, letting the full intervention lifecycle be exercised end-to-end.
 */
public interface SimulateInterventionPrescriptionCommandService {

    /**
     * Issues a default prescription for the accepted proposal of the given request.
     * Idempotent: returns the existing prescription if one was already issued.
     *
     * @param interventionRequestId the accepted intervention request
     * @return the issued (PRESCRIBED) prescription, or empty when there is no
     *         accepted proposal to prescribe for
     */
    Optional<TreatmentPrescription> simulateForRequest(Long interventionRequestId);
}
