package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionOutcomeQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionOutcomeByExecutionIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionOutcomeByIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionOutcomeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link InterventionOutcomeQueryService}.
 */
@Service
public class InterventionOutcomeQueryServiceImpl implements InterventionOutcomeQueryService {

    private final InterventionOutcomeRepository interventionOutcomeRepository;

    public InterventionOutcomeQueryServiceImpl(InterventionOutcomeRepository interventionOutcomeRepository) {
        this.interventionOutcomeRepository = interventionOutcomeRepository;
    }

    @Override
    public Optional<InterventionOutcome> handle(GetInterventionOutcomeByIdQuery query) {
        return interventionOutcomeRepository.findById(query.interventionOutcomeId());
    }

    @Override
    public Optional<InterventionOutcome> handle(GetInterventionOutcomeByExecutionIdQuery query) {
        return interventionOutcomeRepository.findByInterventionExecutionId(new InterventionExecutionId(query.interventionExecutionId()));
    }
}
