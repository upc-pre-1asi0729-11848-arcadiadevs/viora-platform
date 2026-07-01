package com.arcadiadevs.viora.platform.intervention.application.internal.outboundservices.acl;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionExecutionRepository;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers.InterventionExecutionEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories.InterventionExecutionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InterventionExecutionRepositoryImpl implements InterventionExecutionRepository {

    private final InterventionExecutionJpaRepository jpaRepository;

    public InterventionExecutionRepositoryImpl(InterventionExecutionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InterventionExecution save(InterventionExecution interventionExecution) {
        var entity = InterventionExecutionEntityAssembler.toEntity(interventionExecution);
        var savedEntity = jpaRepository.save(entity);
        return InterventionExecutionEntityAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<InterventionExecution> findById(Long id) {
        return jpaRepository.findById(id).map(InterventionExecutionEntityAssembler::toDomain);
    }

    @Override
    public Optional<InterventionExecution> findByTreatmentPrescriptionId(TreatmentPrescriptionId treatmentPrescriptionId) {
        return jpaRepository.findByTreatmentPrescriptionId(treatmentPrescriptionId)
                .map(InterventionExecutionEntityAssembler::toDomain);
    }

    @Override
    public boolean existsByTreatmentPrescriptionId(TreatmentPrescriptionId treatmentPrescriptionId) {
        return jpaRepository.existsByTreatmentPrescriptionId(treatmentPrescriptionId);
    }
}
