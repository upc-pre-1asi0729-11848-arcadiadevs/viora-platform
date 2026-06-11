package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Resource for MitigationRecommendation.
 */
public record MitigationRecommendationResource(
        String actionType,
        String nutritionInputRecommendation,
        LocalDate applicationWindowStart,
        LocalDate applicationWindowEnd
) {
}