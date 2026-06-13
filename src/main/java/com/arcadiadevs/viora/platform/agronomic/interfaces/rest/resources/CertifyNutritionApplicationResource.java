package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request body to certify a dynamic nutrition plan application.
 */
public record CertifyNutritionApplicationResource(
        LocalDate applicationDate,
        LocalTime applicationTime,
        List<String> appliedInputs,
        String doseConfirmation,
        String fieldOperator,
        String fieldNotes
) {
}
