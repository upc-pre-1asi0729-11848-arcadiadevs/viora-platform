package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Resource describing the certified execution of a dynamic nutrition plan.
 */
public record NutritionApplicationResource(
        LocalDate applicationDate,
        LocalTime applicationTime,
        List<String> appliedInputs,
        String doseConfirmation,
        String fieldOperator,
        String fieldNotes
) {
}
