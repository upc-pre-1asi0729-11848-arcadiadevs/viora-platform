package com.arcadiadevs.viora.platform.surveillance.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.surveillance.application.commandservices.AlertCommandService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreateAlertCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.events.PestSightingReportEvaluatedEvent;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Application-layer event handler for {@link PestSightingReportEvaluatedEvent}.
 *
 * <p>When a manual pest sighting report is evaluated and confirmed in the {@code surveillance} 
 * bounded context, this handler automatically provisions a corresponding {@code Alert} record.
 * This removes the need for {@code PestSightingCommandService} to directly orchestrate alert creation.</p>
 *
 * <p>Because Spring's {@link EventListener} dispatches events synchronously by
 * default, the alert record is fully persisted before control returns to the
 * caller that triggered the report evaluation.</p>
 */
@Service("surveillancePestSightingReportEvaluatedEventHandler")
@Slf4j
public class PestSightingReportEvaluatedEventHandler {
    
    private final AlertCommandService alertCommandService;

    /**
     * Constructor.
     *
     * @param alertCommandService the alert command service
     * @see AlertCommandService
     */
    public PestSightingReportEvaluatedEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    /**
     * Handles the {@link PestSightingReportEvaluatedEvent}.
     *
     * <p>Creates an {@code Alert} record if the evaluated pest sighting was confirmed.
     * Delegates to {@link AlertCommandService#handle(CreateAlertCommand)}.</p>
     *
     * @param event the {@link PestSightingReportEvaluatedEvent} published by the {@code surveillance} context
     */
    @EventListener
    public void on(PestSightingReportEvaluatedEvent event) {
        if (!event.isAlertConfirmed()) {
            log.info("Pest sighting report {} evaluated but not confirmed. Alert creation skipped.", event.getReportId());
            return;
        }

        log.info("Pest sighting report {} confirmed. Triggering alert creation.", event.getReportId());

        var command = new CreateAlertCommand(
                event.getPlotId(),
                ThreatType.valueOf(event.getProbableThreat()),
                AlertSeverity.valueOf(event.getCalculatedRisk()),
                "Confirmed pest threat detected",
                "A manual report was evaluated and confirmed to be a significant threat for the plot. Immediate inspection is recommended.",
                List.of(AlertSource.MANUAL_REPORT),
                List.of("Viora Manual Reporting"),
                Map.of("Report ID", String.valueOf(event.getReportId()))
        );
        
        var result = alertCommandService.handle(command);

        if (result.isFailure()) {
            log.warn("Failed to create alert for plot {}: {}", event.getPlotId(), result.failure().get().message());
        }
    }
}
