package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Resource for NutritionApplicationWindow.
 */
public record NutritionApplicationWindowResource(
        LocalDate startDate,
        LocalDate endDate
) {
}
