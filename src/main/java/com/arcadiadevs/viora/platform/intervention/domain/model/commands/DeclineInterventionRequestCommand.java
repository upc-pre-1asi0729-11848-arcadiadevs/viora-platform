package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

/**
 * Command to decline an existing intervention request.
 */
public record DeclineInterventionRequestCommand(
        Long interventionRequestId,
        String reason
) {
    public DeclineInterventionRequestCommand {
        if (interventionRequestId == null || interventionRequestId <= 0) {
            throw new IllegalArgumentException("Intervention Request ID is required and must be positive.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Decline reason is required.");
        }
    }
}
