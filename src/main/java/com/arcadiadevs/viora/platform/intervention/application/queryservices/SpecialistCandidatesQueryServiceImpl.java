package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetSpecialistCandidatesByAlertIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.services.SpecialistCandidatesQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.services.SpecialistMatchingPolicy;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.SpecialistCandidateResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link SpecialistCandidatesQueryService}.
 */
@Service
public class SpecialistCandidatesQueryServiceImpl implements SpecialistCandidatesQueryService {

    private final SpecialistMatchingPolicy specialistMatchingPolicy;

    public SpecialistCandidatesQueryServiceImpl(SpecialistMatchingPolicy specialistMatchingPolicy) {
        this.specialistMatchingPolicy = specialistMatchingPolicy;
    }

    @Override
    public List<SpecialistCandidateResource> handle(GetSpecialistCandidatesByAlertIdQuery query) {
        return specialistMatchingPolicy.matchSpecialistsForAlert(query.alertId(), query.limit());
    }
}
