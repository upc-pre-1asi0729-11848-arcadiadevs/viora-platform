package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import java.util.List;

/**
 * Resource representing a specialist candidate for an intervention.
 */
public record SpecialistCandidateResource(
        Long id,
        String name,
        Double successRate,
        Integer caseCount,
        Double distanceKm,
        String experience,
        List<String> tags,
        Boolean available
) {
}
