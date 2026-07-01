package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.InterventionOutcomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterventionOutcomeJpaRepository extends JpaRepository<InterventionOutcomeEntity, Long> {
    Optional<InterventionOutcomeEntity> findByInterventionExecutionId(InterventionExecutionId interventionExecutionId);
    boolean existsByInterventionExecutionId(InterventionExecutionId interventionExecutionId);
}
