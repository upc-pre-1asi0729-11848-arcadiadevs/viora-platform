package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionRequestRepository;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers.InterventionRequestEntityAssembler;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories.InterventionRequestJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaInterventionRequestRepositoryAdapter implements InterventionRequestRepository {

    private final InterventionRequestJpaRepository jpaRepository;

    public JpaInterventionRequestRepositoryAdapter(InterventionRequestJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InterventionRequest save(InterventionRequest interventionRequest) {
        var entity = InterventionRequestEntityAssembler.toEntity(interventionRequest);
        var savedEntity = jpaRepository.save(entity);
        return InterventionRequestEntityAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<InterventionRequest> findById(InterventionRequestId id) {
        return jpaRepository.findById(id.value())
                .map(InterventionRequestEntityAssembler::toDomain);
    }

    @Override
    public List<InterventionRequest> findByGrowerId(Long growerId) {
        return jpaRepository.findByGrowerId(growerId).stream()
                .map(InterventionRequestEntityAssembler::toDomain)
                .toList();
    }

    @Override
    public List<InterventionRequest> findBySpecialistIdAndStatus(Long specialistId, InterventionStatus status) {
        return jpaRepository.findBySpecialistIdAndStatus(specialistId, status).stream()
                .map(InterventionRequestEntityAssembler::toDomain)
                .toList();
    }
}
