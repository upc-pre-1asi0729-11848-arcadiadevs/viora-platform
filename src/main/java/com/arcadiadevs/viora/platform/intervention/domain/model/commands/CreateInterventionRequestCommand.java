package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

/**
 * Command to create a new intervention request.
 */
public record CreateInterventionRequestCommand(
        Long growerId,
        Long specialistId,
        Long alertId,
        String reason,
        String message
) {
    public CreateInterventionRequestCommand {
        if (growerId == null || growerId <= 0) {
            throw new IllegalArgumentException("Grower ID is required and must be positive.");
        }
        if (specialistId == null || specialistId <= 0) {
            throw new IllegalArgumentException("Specialist ID is required and must be positive.");
        }
        if (alertId == null || alertId <= 0) {
            throw new IllegalArgumentException("Alert ID is required and must be positive.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }
    }
}
