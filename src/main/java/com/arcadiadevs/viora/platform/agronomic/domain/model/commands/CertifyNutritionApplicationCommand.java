package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Command to certify the in-field execution of a dynamic nutrition plan.
 *
 * @param userId Owner user identifier (until IAM supplies the principal).
 * @param planId Dynamic nutrition plan identifier.
 * @param applicationDate Date the inputs were applied.
 * @param applicationTime Time the inputs were applied.
 * @param appliedInputs Names of the plan inputs that were applied.
 * @param doseConfirmation Dose confirmation (AS_RECOMMENDED or ADJUSTED).
 * @param fieldOperator Operator or crew that performed the application.
 * @param fieldNotes Optional field observations.
 */
public record CertifyNutritionApplicationCommand(
        Long userId,
        Long planId,
        LocalDate applicationDate,
        LocalTime applicationTime,
        List<String> appliedInputs,
        String doseConfirmation,
        String fieldOperator,
        String fieldNotes
) {
    public CertifyNutritionApplicationCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be a positive number.");
        }
        if (applicationDate == null) {
            throw new IllegalArgumentException("Application date is required.");
        }
        if (applicationTime == null) {
            throw new IllegalArgumentException("Application time is required.");
        }
        if (appliedInputs == null || appliedInputs.isEmpty()) {
            throw new IllegalArgumentException("At least one applied input is required.");
        }
        if (doseConfirmation == null || doseConfirmation.isBlank()) {
            throw new IllegalArgumentException("Dose confirmation is required.");
        }
        if (fieldOperator == null || fieldOperator.isBlank()) {
            throw new IllegalArgumentException("Field operator is required.");
        }
        appliedInputs = List.copyOf(appliedInputs);
    }
}
