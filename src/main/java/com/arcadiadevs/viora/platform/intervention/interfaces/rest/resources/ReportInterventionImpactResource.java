package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ImpactLevel;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ObservedResult;

public record ReportInterventionImpactResource(
        Long interventionExecutionId,
        String gracePeriod,
        String observedResult,
        String impactLevel,
        String producerAssessment
) {}
