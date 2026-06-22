package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import org.jspecify.annotations.NullMarked;
import java.util.List;

/**
 * Information about a pest sighting report.
 *
 * @param id               Unique identifier of the report
 * @param plotId           ID of the plot where the sighting occurred
 * @param reporterUserId   ID of the user reporting the sighting
 * @param riskZone         Zone within the plot affected
 * @param symptoms         List of symptom IDs observed
 * @param observedSeverity Observed severity of the symptoms
 * @param notes            Additional notes or observations
 * @param evaluated        Indicates whether this report has been automatically evaluated
 * @param calculatedRisk   The calculated risk severity if evaluated.
 * @param probableThreat   The identified probable threat if evaluated.
 * @param status           The triage outcome of the report (LOGGED, NEEDS_INSPECTION, CONFIRMED).
 * @param alertConfirmed   Whether an alert was generated based on this report.
 */
@NullMarked
public record PestSightingReportResource(
        Long id,
        Long plotId,
        Long reporterUserId,
        String riskZone,
        List<String> symptoms,
        String observedSeverity,
        String notes,
        boolean evaluated,
        String calculatedRisk,
        String probableThreat,
        String status,
        boolean alertConfirmed
) {}
