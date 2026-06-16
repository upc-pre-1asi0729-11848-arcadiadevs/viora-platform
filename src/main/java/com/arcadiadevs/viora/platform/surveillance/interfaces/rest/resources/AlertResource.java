package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import java.util.List;
import java.util.Map;

public record AlertResource(
        Long id,
        Long plotId,
        String type,
        String severity,
        String status,
        String title,
        String riskExplanation,
        List<String> sources,
        List<String> dataProviders,
        Map<String, String> supportingData,
        List<AlertTimelineRecordResource> timeline
) {
}
