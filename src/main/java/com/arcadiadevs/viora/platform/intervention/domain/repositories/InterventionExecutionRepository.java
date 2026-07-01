package com.arcadiadevs.viora.platform.intervention.domain.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;

import java.util.Optional;

public interface InterventionExecutionRepository {
    InterventionExecution save(InterventionExecution interventionExecution);
    Optional<InterventionExecution> findById(Long id);
    Optional<InterventionExecution> findByTreatmentPrescriptionId(TreatmentPrescriptionId treatmentPrescriptionId);
    boolean existsByTreatmentPrescriptionId(TreatmentPrescriptionId treatmentPrescriptionId);
}
