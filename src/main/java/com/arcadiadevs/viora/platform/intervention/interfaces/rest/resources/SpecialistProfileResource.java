package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import java.util.List;

/**
 * Public specialist profile (safe to show before a proposal is accepted).
 */
public record SpecialistProfileResource(
        Long id,
        String fullName,
        String role,
        Double successRate,
        Integer caseCount,
        Double distanceKm,
        List<String> tags,
        String availability
) {
}
