package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.TreatmentPrescriptionRepository;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers.TreatmentPrescriptionEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories.TreatmentPrescriptionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA adapter for the TreatmentPrescriptionRepository.
 */
@Repository
public class JpaTreatmentPrescriptionRepositoryAdapter implements TreatmentPrescriptionRepository {

    private final TreatmentPrescriptionJpaRepository repository;

    public JpaTreatmentPrescriptionRepositoryAdapter(TreatmentPrescriptionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TreatmentPrescription save(TreatmentPrescription treatmentPrescription) {
        var entity = TreatmentPrescriptionEntityAssembler.toEntity(treatmentPrescription);
        var savedEntity = repository.save(entity);
        return TreatmentPrescriptionEntityAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<TreatmentPrescription> findById(TreatmentPrescriptionId id) {
        return repository.findById(id.value())
                .map(TreatmentPrescriptionEntityAssembler::toDomain);
    }

    @Override
    public Optional<TreatmentPrescription> findByServiceProposalId(Long serviceProposalId) {
        return repository.findByServiceProposalId(serviceProposalId)
                .map(TreatmentPrescriptionEntityAssembler::toDomain);
    }
}
