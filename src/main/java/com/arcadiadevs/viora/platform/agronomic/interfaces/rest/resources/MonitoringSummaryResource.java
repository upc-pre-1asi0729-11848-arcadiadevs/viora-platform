package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource for MonitoringSummary.
 */
public record MonitoringSummaryResource(
        Long monitoringSummaryId,
        Long userId,
        String generalHealthStatus,
        Double ndviValue,
        Double accumulatedChillHours,
        Double yieldForecast,
        LocalDate measurementDate,
        WeatherSnapshotResource weatherSnapshot,
        String climateRiskLevel,
        List<MitigationRecommendationResource> mitigationRecommendations
) {
}