package com.arcadiadevs.viora.platform.surveillance.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.agronomic.interfaces.events.HydricStressDetectedIntegrationEvent;
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
 * Application-layer event handler for {@link HydricStressDetectedIntegrationEvent}.
 */
@Service("surveillanceAgronomicHydricStressEventHandler")
@Slf4j
public class AgronomicHydricStressEventHandler {

    private final AlertCommandService alertCommandService;

    public AgronomicHydricStressEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(HydricStressDetectedIntegrationEvent event) {
        log.info("Received HydricStressDetectedIntegrationEvent for plot {}. Generating Alert...", event.getPlotId());

        var command = new CreateAlertCommand(
                event.getPlotId(),
                ThreatType.WATER_STRESS,
                AlertSeverity.HIGH,
                "Hydric stress warning",
                "Soil moisture levels have dropped below critical thresholds. Immediate irrigation is recommended.",
                List.of(AlertSource.IOT),
                List.of("Viora Sensors"),
                Map.of(
                        "Sensor ID", event.getSensorId(),
                        "Current Moisture", String.valueOf(event.getCurrentMoisture()),
                        "Threshold", String.valueOf(event.getThreshold())
                )
        );

        var result = alertCommandService.handle(command);

        if (result.isFailure()) {
            log.warn("Failed to create alert for plot {}: {}", event.getPlotId(), result.failure().get().message());
        }
    }
}
