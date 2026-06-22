package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

/**
 * Command to confirm (escalate) the alert originated by a pest sighting report,
 * after a grower's field inspection corroborated the threat.
 *
 * @param reportId The originating pest sighting report id.
 */
public record ConfirmAlertFromInspectionCommand(
        Long reportId
) {
}
