package com.arcadiadevs.viora.platform.agronomic.interfaces.events;

import org.springframework.context.ApplicationEvent;

public class DynamicNutritionPlanGeneratedIntegrationEvent extends ApplicationEvent {
    private final Long planId;
    private final Long plotId;
    private final Long alertId;

    public DynamicNutritionPlanGeneratedIntegrationEvent(Object source, Long planId, Long plotId, Long alertId) {
        super(source);
        this.planId = planId;
        this.plotId = plotId;
        this.alertId = alertId;
    }

    public Long getPlanId() {
        return planId;
    }

    public Long getPlotId() {
        return plotId;
    }

    public Long getAlertId() {
        return alertId;
    }
}
