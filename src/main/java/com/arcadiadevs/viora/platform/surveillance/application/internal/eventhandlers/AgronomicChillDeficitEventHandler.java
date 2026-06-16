package com.arcadiadevs.viora.platform.surveillance.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.agronomic.interfaces.events.ChillDeficitIntegrationEvent;
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
 * Application-layer event handler for {@link ChillDeficitIntegrationEvent}.
 */
@Service("surveillanceAgronomicChillDeficitEventHandler")
@Slf4j
public class AgronomicChillDeficitEventHandler {

    private final AlertCommandService alertCommandService;

    public AgronomicChillDeficitEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(ChillDeficitIntegrationEvent event) {
        log.info("Received ChillDeficitIntegrationEvent for plot {}. Generating Alert...", event.getPlotId());

        double gap = event.getCurrentChillAccumulation() - event.getTargetChill();

        var command = new CreateAlertCommand(
                event.getPlotId(),
                ThreatType.CLIMATE_EXTREME,
                AlertSeverity.HIGH,
                "Chill deficit warning",
                "The plot is accumulating fewer chill portions than expected. Persistent warm conditions may disrupt flowering uniformity.",
                List.of(AlertSource.CLIMATE),
                List.of("AgroMonitoring", "Viora model"),
                Map.of(
                        "Current chill accumulation", event.getCurrentChillAccumulation() + " CP",
                        "Target for current stage", event.getTargetChill() + " CP",
                        "Gap", gap + " CP",
                        "Temperature anomaly", (event.getTemperatureAnomaly() > 0 ? "+" : "") + event.getTemperatureAnomaly() + "°C"
                )
        );

        var result = alertCommandService.handle(command);

        if (result.isFailure()) {
            log.warn("Failed to create alert for plot {}: {}", event.getPlotId(), result.failure().get().message());
        }
    }
}
