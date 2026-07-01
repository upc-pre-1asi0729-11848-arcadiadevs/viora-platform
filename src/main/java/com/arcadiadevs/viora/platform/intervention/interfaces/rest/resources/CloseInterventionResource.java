package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.HireAgain;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceResult;

public record CloseInterventionResource(
        String serviceResult,
        String hireAgain,
        String privateFeedback
) {}
