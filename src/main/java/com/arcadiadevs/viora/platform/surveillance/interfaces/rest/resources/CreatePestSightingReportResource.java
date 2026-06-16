package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;
import java.util.List;

/**
 * Data required to create a new manual pest sighting report.
 *
 * @param plotId           ID of the plot where the sighting occurred
 * @param reporterUserId   ID of the user reporting the sighting
 * @param riskZone         Zone within the plot affected (e.g. FULL_PLOT)
 * @param symptoms         List of symptom IDs observed
 * @param observedSeverity Observed severity of the symptoms
 * @param notes            Additional notes or observations from the reporter
 */
@NullMarked
public record CreatePestSightingReportResource(
        @NotNull Long plotId,
        @NotNull Long reporterUserId,
        @NotBlank String riskZone,
        @NotEmpty List<String> symptoms,
        @NotBlank String observedSeverity,
        String notes
) {}
