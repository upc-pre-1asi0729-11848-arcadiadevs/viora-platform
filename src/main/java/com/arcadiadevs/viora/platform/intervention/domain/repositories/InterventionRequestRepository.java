package com.arcadiadevs.viora.platform.intervention.domain.repositories;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository for InterventionRequest.
 */
public interface InterventionRequestRepository {

    InterventionRequest save(InterventionRequest interventionRequest);

    Optional<InterventionRequest> findById(InterventionRequestId id);

    List<InterventionRequest> findByGrowerId(Long growerId);

    List<InterventionRequest> findBySpecialistIdAndStatus(Long specialistId, InterventionStatus status);
}
