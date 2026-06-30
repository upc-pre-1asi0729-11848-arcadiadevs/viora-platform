package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetTreatmentPrescriptionByIdQuery;

import java.util.Optional;

/**
 * Query service for Treatment Prescription.
 */
public interface TreatmentPrescriptionQueryService {
    Optional<TreatmentPrescription> handle(GetTreatmentPrescriptionByIdQuery query);
}
