package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record ServiceEvaluation(
        ServiceResult serviceResult,
        HireAgain hireAgain,
        String privateFeedback
) {
    public ServiceEvaluation {
        if (serviceResult == null) {
            throw new IllegalArgumentException("Service result must be provided");
        }
        if (hireAgain == null) {
            throw new IllegalArgumentException("Hire again decision must be provided");
        }
    }
}
