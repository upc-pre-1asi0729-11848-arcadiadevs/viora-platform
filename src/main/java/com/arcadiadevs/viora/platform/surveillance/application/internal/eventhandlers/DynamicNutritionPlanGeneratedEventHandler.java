package com.arcadiadevs.viora.platform.surveillance.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.agronomic.interfaces.events.DynamicNutritionPlanGeneratedIntegrationEvent;
import com.arcadiadevs.viora.platform.surveillance.application.commandservices.AlertCommandService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.AddAlertTimelineRecordCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler that listens to Agronomic events indicating a Dynamic Nutrition Plan
 * was automatically generated. It uses this information to update the timeline of
 * the relevant Alert.
 */
@Service
@Slf4j
public class DynamicNutritionPlanGeneratedEventHandler {

    private final AlertCommandService alertCommandService;

    public DynamicNutritionPlanGeneratedEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(DynamicNutritionPlanGeneratedIntegrationEvent event) {
        if (event.getAlertId() != null) {
            log.info("Received DynamicNutritionPlanGeneratedIntegrationEvent for Plan {} associated with Alert {}. Updating timeline...", event.getPlanId(), event.getAlertId());
            
            var command = new AddAlertTimelineRecordCommand(
                    event.getAlertId(),
                    "Plan",
                    "Dynamic Nutrition Plan Generated",
                    "An automated Dynamic Nutrition Plan (ID: " + event.getPlanId() + ") has been recommended to mitigate this risk."
            );
            
            var result = alertCommandService.handle(command);
            if (result.isSuccess()) {
                log.info("Successfully added timeline record for Alert {}.", event.getAlertId());
            } else {
                log.error("Failed to add timeline record for Alert {}: {}", event.getAlertId(), result.failure().get().message());
            }
        } else {
            log.debug("Received DynamicNutritionPlanGeneratedIntegrationEvent for Plan {} but no Alert ID was associated (likely manual generation).", event.getPlanId());
        }
    }
}
