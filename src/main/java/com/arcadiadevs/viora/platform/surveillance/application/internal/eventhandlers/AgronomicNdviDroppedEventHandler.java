package com.arcadiadevs.viora.platform.surveillance.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.agronomic.interfaces.events.NdviDroppedIntegrationEvent;
import com.arcadiadevs.viora.platform.surveillance.application.commandservices.AlertCommandService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreateAlertCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Application-layer event handler for {@link NdviDroppedIntegrationEvent}.
 */
@Service("surveillanceAgronomicNdviDroppedEventHandler")
@Slf4j
public class AgronomicNdviDroppedEventHandler {

    private final AlertCommandService alertCommandService;

    public AgronomicNdviDroppedEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(NdviDroppedIntegrationEvent event) {
        log.info("Received NdviDroppedIntegrationEvent for plot {}. Generating Alert...", event.getPlotId());

        var command = new CreateAlertCommand(
                event.getPlotId(),
                ThreatType.PHENOLOGICAL_RISK,
                AlertSeverity.MEDIUM,
                "Low NDVI zone detected",
                "The plot's NDVI has dropped below the safe threshold, suggesting possible phenological risks or crop stress.",
                List.of(AlertSource.SATELLITE),
                List.of("AgroMonitoring", "Viora model"),
                Map.of(
                        "Current NDVI", String.valueOf(event.getCurrentNdvi()),
                        "Historical Average", String.valueOf(event.getHistoricalAverage())
                )
        );

        var result = alertCommandService.handle(command);

        if (result.isFailure()) {
            log.warn("Failed to create alert for plot {}: {}", event.getPlotId(), result.failure().get().message());
        }
    }
}
