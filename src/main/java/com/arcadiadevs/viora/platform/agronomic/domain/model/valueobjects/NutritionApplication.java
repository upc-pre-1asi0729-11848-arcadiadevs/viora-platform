package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Records the in-field execution (certification) of a dynamic nutrition plan.
 *
 * <p>
 * Captures when the plan was applied, which of its inputs were actually used,
 * whether the dosage followed the recommendation, and by whom. This executed
 * action is what updates the plot history and feeds the predictive engine.
 * </p>
 *
 * @param applicationDate Date the inputs were applied in field.
 * @param applicationTime Time the inputs were applied.
 * @param appliedInputs Names of the plan inputs that were actually applied; never empty.
 * @param doseConfirmation Whether the dosage followed the recommendation.
 * @param fieldOperator Operator or crew that performed the application.
 * @param fieldNotes Optional field observations.
 */
public record NutritionApplication(
        LocalDate applicationDate,
        LocalTime applicationTime,
        List<String> appliedInputs,
        DoseConfirmation doseConfirmation,
        String fieldOperator,
        String fieldNotes
) {
    public NutritionApplication {
        if (applicationDate == null) {
            throw new IllegalArgumentException("Application date is required.");
        }
        if (applicationTime == null) {
            throw new IllegalArgumentException("Application time is required.");
        }
        if (appliedInputs == null || appliedInputs.isEmpty()) {
            throw new IllegalArgumentException("At least one applied input is required to certify an application.");
        }
        if (appliedInputs.stream().anyMatch(input -> input == null || input.isBlank())) {
            throw new IllegalArgumentException("Applied input names cannot be blank.");
        }
        if (doseConfirmation == null) {
            throw new IllegalArgumentException("Dose confirmation is required.");
        }
        if (fieldOperator == null || fieldOperator.isBlank()) {
            throw new IllegalArgumentException("Field operator is required.");
        }
        appliedInputs = List.copyOf(appliedInputs);
    }
}
