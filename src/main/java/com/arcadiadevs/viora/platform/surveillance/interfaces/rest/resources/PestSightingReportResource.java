package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import java.util.List;

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
        String probableThreat
) {
}
