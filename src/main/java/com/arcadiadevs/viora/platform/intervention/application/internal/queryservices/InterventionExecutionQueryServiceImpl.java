package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionExecutionQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionExecutionByIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InterventionExecutionQueryServiceImpl implements InterventionExecutionQueryService {

    private final InterventionExecutionRepository interventionExecutionRepository;

    public InterventionExecutionQueryServiceImpl(InterventionExecutionRepository interventionExecutionRepository) {
        this.interventionExecutionRepository = interventionExecutionRepository;
    }

    @Override
    public Optional<InterventionExecution> handle(GetInterventionExecutionByIdQuery query) {
        return interventionExecutionRepository.findById(query.interventionExecutionId());
    }
}
