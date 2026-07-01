package com.arcadiadevs.viora.platform.intervention.domain.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;

import java.util.Optional;

public interface InterventionOutcomeRepository {
    InterventionOutcome save(InterventionOutcome interventionOutcome);
    Optional<InterventionOutcome> findById(Long id);
    Optional<InterventionOutcome> findByInterventionExecutionId(InterventionExecutionId interventionExecutionId);
    boolean existsByInterventionExecutionId(InterventionExecutionId interventionExecutionId);
}
