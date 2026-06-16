package com.arcadiadevs.viora.platform.agronomic.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.DynamicNutritionPlanCommandService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.RecommendDynamicNutritionCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.surveillance.interfaces.events.AlertGeneratedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler in Agronomic module that listens for Alerts generated in Surveillance.
 */
@Service("agronomicAlertGeneratedEventHandler")
@Slf4j
public class AlertGeneratedEventHandler {

    private final DynamicNutritionPlanCommandService dynamicNutritionPlanCommandService;
    private final PlotRepository plotRepository;

    public AlertGeneratedEventHandler(DynamicNutritionPlanCommandService dynamicNutritionPlanCommandService, PlotRepository plotRepository) {
        this.dynamicNutritionPlanCommandService = dynamicNutritionPlanCommandService;
        this.plotRepository = plotRepository;
    }

    @EventListener
    public void on(AlertGeneratedIntegrationEvent event) {
        if ("PHENOLOGICAL_RISK".equals(event.getAlertType())) {
            log.info("Received AlertGeneratedIntegrationEvent for Phenological Risk on plot {}. Triggering Dynamic Nutrition Plan...", event.getPlotId());

            // We need the user ID to generate the plan. Let's get it from the Plot owner.
            plotRepository.findById(new PlotId(event.getPlotId())).ifPresentOrElse(plot -> {
                var command = new RecommendDynamicNutritionCommand(plot.getUserId().getValue(), plot.getId().getValue());
                var result = dynamicNutritionPlanCommandService.handle(command);
                
                if (result.isSuccess()) {
                    log.info("Successfully generated Automated Dynamic Nutrition Plan for plot {}", event.getPlotId());
                } else {
                    log.error("Failed to generate automated Dynamic Nutrition Plan: {}", result.failure().get().message());
                }
            }, () -> {
                log.error("Could not find Plot {} to generate automated Dynamic Nutrition Plan", event.getPlotId());
            });
        }
    }
}
