package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

public record PrescribedProductResource(
        String productName,
        Double dosageAmount,
        String dosageUnit,
        Integer sessionsCount,
        String technicalRecommendation
) {}
