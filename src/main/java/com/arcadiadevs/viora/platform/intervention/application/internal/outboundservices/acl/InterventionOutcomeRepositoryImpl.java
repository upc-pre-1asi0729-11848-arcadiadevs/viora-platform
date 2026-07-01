package com.arcadiadevs.viora.platform.intervention.application.internal.outboundservices.acl;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionOutcomeRepository;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers.InterventionOutcomeEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories.InterventionOutcomeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InterventionOutcomeRepositoryImpl implements InterventionOutcomeRepository {

    private final InterventionOutcomeJpaRepository jpaRepository;

    public InterventionOutcomeRepositoryImpl(InterventionOutcomeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InterventionOutcome save(InterventionOutcome interventionOutcome) {
        var entity = InterventionOutcomeEntityAssembler.toEntity(interventionOutcome);
        var savedEntity = jpaRepository.save(entity);
        return InterventionOutcomeEntityAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<InterventionOutcome> findById(Long id) {
        return jpaRepository.findById(id).map(InterventionOutcomeEntityAssembler::toDomain);
    }

    @Override
    public Optional<InterventionOutcome> findByInterventionExecutionId(InterventionExecutionId interventionExecutionId) {
        return jpaRepository.findByInterventionExecutionId(interventionExecutionId)
                .map(InterventionOutcomeEntityAssembler::toDomain);
    }

    @Override
    public boolean existsByInterventionExecutionId(InterventionExecutionId interventionExecutionId) {
        return jpaRepository.existsByInterventionExecutionId(interventionExecutionId);
    }
}
