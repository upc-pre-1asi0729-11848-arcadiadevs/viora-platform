package com.arcadiadevs.viora.platform.surveillance.domain.model.commands;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;

import java.util.List;
import java.util.Map;

/**
 * Command to create a new Alert in the system.
 */
public record CreateAlertCommand(
        Long plotId,
        ThreatType alertType,
        AlertSeverity severity,
        String title,
        String riskExplanation,
        List<AlertSource> sources,
        List<String> dataProviders,
        Map<String, String> supportingData
) {
    public CreateAlertCommand {
        if (plotId == null) throw new IllegalArgumentException("Plot ID is required");
        if (alertType == null) throw new IllegalArgumentException("Alert type is required");
        if (severity == null) throw new IllegalArgumentException("Severity is required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (riskExplanation == null || riskExplanation.isBlank()) throw new IllegalArgumentException("Risk explanation is required");
    }
}
