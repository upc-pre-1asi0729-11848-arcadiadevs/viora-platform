package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.HireAgain;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceResult;

public record CloseInterventionCommand(
        Long interventionOutcomeId,
        ServiceResult serviceResult,
        HireAgain hireAgain,
        String privateFeedback
) {
    public CloseInterventionCommand {
        if (interventionOutcomeId == null || interventionOutcomeId <= 0) {
            throw new IllegalArgumentException("Intervention outcome ID must be provided and positive");
        }
        if (serviceResult == null) {
            throw new IllegalArgumentException("Service result must be provided");
        }
        if (hireAgain == null) {
            throw new IllegalArgumentException("Hire again decision must be provided");
        }
    }
}
