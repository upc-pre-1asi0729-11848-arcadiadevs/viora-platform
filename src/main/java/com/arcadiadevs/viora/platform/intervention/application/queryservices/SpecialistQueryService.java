package com.arcadiadevs.viora.platform.intervention.application.queryservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;

import java.util.Optional;

/**
 * Read operations for the Specialist aggregate.
 */
public interface SpecialistQueryService {

    /** Returns the public profile of a specialist. */
    Optional<Specialist> getProfile(Long specialistId);

    /**
     * Returns the specialist's private contact details, but only when the given
     * request has been accepted for that specialist. Returns empty otherwise so
     * the interface layer can respond with 403/404.
     *
     * @param specialistId the specialist whose contact is requested
     * @param requestId    the accepted intervention request that unlocks contact
     */
    Optional<Specialist> getContactForAcceptedRequest(Long specialistId, Long requestId);
}
