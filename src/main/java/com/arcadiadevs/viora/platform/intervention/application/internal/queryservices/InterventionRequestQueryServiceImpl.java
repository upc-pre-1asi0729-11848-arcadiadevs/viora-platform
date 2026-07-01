package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionRequestsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetInterventionRequestByIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionRequestQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterventionRequestQueryServiceImpl implements InterventionRequestQueryService {

    private final InterventionRequestRepository interventionRequestRepository;

    public InterventionRequestQueryServiceImpl(InterventionRequestRepository interventionRequestRepository) {
        this.interventionRequestRepository = interventionRequestRepository;
    }

    @Override
    public Optional<InterventionRequest> handle(GetInterventionRequestByIdQuery query) {
        return interventionRequestRepository.findById(new InterventionRequestId(query.interventionRequestId()));
    }

    @Override
    public List<InterventionRequest> handle(GetGrowerInterventionRequestsQuery query) {
        if (query.plotId() != null) {
            return interventionRequestRepository.findByGrowerIdAndPlotId(query.growerId(), query.plotId());
        }
        return interventionRequestRepository.findByGrowerId(query.growerId());
    }
}
