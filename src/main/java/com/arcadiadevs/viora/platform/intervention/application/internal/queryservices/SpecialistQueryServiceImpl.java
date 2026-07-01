package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.SpecialistQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionRequestRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpecialistQueryServiceImpl implements SpecialistQueryService {

    private final SpecialistRepository specialistRepository;
    private final InterventionRequestRepository interventionRequestRepository;

    public SpecialistQueryServiceImpl(
            SpecialistRepository specialistRepository,
            InterventionRequestRepository interventionRequestRepository) {
        this.specialistRepository = specialistRepository;
        this.interventionRequestRepository = interventionRequestRepository;
    }

    @Override
    public Optional<Specialist> getProfile(Long specialistId) {
        return specialistRepository.findById(new SpecialistId(specialistId));
    }

    @Override
    public Optional<Specialist> getContactForAcceptedRequest(Long specialistId, Long requestId) {
        var request = interventionRequestRepository.findById(new InterventionRequestId(requestId));

        boolean unlocked = request
                .filter(r -> r.getStatus() == InterventionStatus.ACCEPTED)
                .filter(r -> r.getSpecialistId() != null && r.getSpecialistId().equals(specialistId))
                .isPresent();

        if (!unlocked) {
            return Optional.empty();
        }

        return specialistRepository.findById(new SpecialistId(specialistId));
    }
}
