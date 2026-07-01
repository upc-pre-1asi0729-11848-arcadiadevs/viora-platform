package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.GracePeriod;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ImpactLevel;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ObservedResult;

public record ReportInterventionImpactCommand(
        Long interventionExecutionId,
        GracePeriod gracePeriod,
        ObservedResult observedResult,
        ImpactLevel impactLevel,
        String producerAssessment
) {
    public ReportInterventionImpactCommand {
        if (interventionExecutionId == null || interventionExecutionId <= 0) {
            throw new IllegalArgumentException("Intervention execution ID must be provided and positive");
        }
        if (gracePeriod == null) {
            throw new IllegalArgumentException("Grace period must be provided");
        }
        if (observedResult == null) {
            throw new IllegalArgumentException("Observed result must be provided");
        }
        if (impactLevel == null) {
            throw new IllegalArgumentException("Impact level must be provided");
        }
    }
}
