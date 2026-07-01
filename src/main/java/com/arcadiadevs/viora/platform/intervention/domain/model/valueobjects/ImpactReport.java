package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record ImpactReport(
        GracePeriod gracePeriod,
        ObservedResult observedResult,
        ImpactLevel impactLevel,
        String producerAssessment
) {
    public ImpactReport {
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
