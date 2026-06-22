package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

/**
 * Command to resolve a pest sighting report after a field inspection.
 *
 * @param reportId       The report to resolve.
 * @param reporterUserId The user resolving it (must own the report).
 * @param outcome        The inspection outcome: {@code CONFIRMED} or {@code RULED_OUT}.
 */
public record ReviewPestSightingReportCommand(
        Long reportId,
        Long reporterUserId,
        String outcome
) {
}
