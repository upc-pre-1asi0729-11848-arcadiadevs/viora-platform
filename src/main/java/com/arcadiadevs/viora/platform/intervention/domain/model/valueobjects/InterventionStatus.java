package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Represents the status of an intervention request.
 */
public enum InterventionStatus {
    PENDING,
    AWAITING_RESPONSE,
    PROPOSAL_RECEIVED,
    ACCEPTED,
    DECLINED
}
