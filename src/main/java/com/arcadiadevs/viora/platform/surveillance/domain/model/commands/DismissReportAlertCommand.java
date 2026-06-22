package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

/**
 * Command to dismiss the alert originated by a pest sighting report, after a grower
 * ruled the threat out as a verified false positive on inspection.
 *
 * @param reportId The originating pest sighting report id.
 * @param reason   Human-readable reason recorded in the alert timeline.
 */
public record DismissReportAlertCommand(
        Long reportId,
        String reason
) {
}
