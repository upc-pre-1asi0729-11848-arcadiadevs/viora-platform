package com.arcadiadevs.viora.platform.surveillance.interfaces.events;

import org.springframework.context.ApplicationEvent;

public class AlertGeneratedIntegrationEvent extends ApplicationEvent {

    private final Long alertId;
    private final Long plotId;
    private final String alertType;

    public AlertGeneratedIntegrationEvent(Object source, Long alertId, Long plotId, String alertType) {
        super(source);
        this.alertId = alertId;
        this.plotId = plotId;
        this.alertType = alertType;
    }

    public Long getAlertId() {
        return alertId;
    }

    public Long getPlotId() {
        return plotId;
    }

    public String getAlertType() {
        return alertType;
    }
}
