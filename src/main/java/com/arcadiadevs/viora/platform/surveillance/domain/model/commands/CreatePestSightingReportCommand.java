package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

import java.util.List;

/**
 * Command to create a manual pest sighting report.
 */
public record CreatePestSightingReportCommand(
        Long plotId,
        Long reporterUserId,
        String riskZone,
        List<String> symptoms,
        String observedSeverity,
        String notes
) {
}
