package com.arcadiadevs.viora.platform.intervention.domain.model.queries;

/**
 * Query to get specialist candidates based on an alert.
 */
public record GetSpecialistCandidatesByAlertIdQuery(Long alertId, Integer limit) {
}
