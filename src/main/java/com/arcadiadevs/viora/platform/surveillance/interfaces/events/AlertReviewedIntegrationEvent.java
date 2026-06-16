package com.arcadiadevs.viora.platform.surveillance.interfaces.events;

import org.springframework.context.ApplicationEvent;

public class AlertReviewedIntegrationEvent extends ApplicationEvent {
    private final Long alertId;

    public AlertReviewedIntegrationEvent(Object source, Long alertId) {
        super(source);
        this.alertId = alertId;
    }

    public Long getAlertId() {
        return alertId;
    }
}
