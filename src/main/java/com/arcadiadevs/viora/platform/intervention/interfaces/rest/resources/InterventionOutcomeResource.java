package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.HireAgain;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ImpactLevel;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ObservedResult;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.OutcomeStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceResult;

public record InterventionOutcomeResource(
        Long id,
        Long interventionExecutionId,
        String gracePeriod,
        String observedResult,
        String impactLevel,
        String producerAssessment,
        String serviceResult,
        String hireAgain,
        String privateFeedback,
        String status
) {}
