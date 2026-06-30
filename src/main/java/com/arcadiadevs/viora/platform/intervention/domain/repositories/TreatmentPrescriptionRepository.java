package com.arcadiadevs.viora.platform.intervention.domain.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;

import java.util.Optional;

/**
 * Repository interface for managing {@link TreatmentPrescription} aggregates.
 */
public interface TreatmentPrescriptionRepository {
    
    /**
     * Saves a treatment prescription.
     *
     * @param treatmentPrescription the aggregate to save
     * @return the saved aggregate
     */
    TreatmentPrescription save(TreatmentPrescription treatmentPrescription);

    /**
     * Finds a treatment prescription by its ID.
     *
     * @param id the treatment prescription ID
     * @return an optional containing the aggregate if found
     */
    Optional<TreatmentPrescription> findById(TreatmentPrescriptionId id);

    /**
     * Finds a treatment prescription by its associated service proposal ID.
     *
     * @param serviceProposalId the associated service proposal ID
     * @return an optional containing the aggregate if found
     */
    Optional<TreatmentPrescription> findByServiceProposalId(Long serviceProposalId);
}
