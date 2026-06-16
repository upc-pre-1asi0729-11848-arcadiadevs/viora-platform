package com.arcadiadevs.viora.platform.agronomic.interfaces.events;

import org.springframework.context.ApplicationEvent;

public class HydricStressDetectedIntegrationEvent extends ApplicationEvent {
    
    private final Long plotId;
    private final String sensorId;
    private final Double currentMoisture;
    private final Double threshold;

    public HydricStressDetectedIntegrationEvent(Object source, Long plotId, String sensorId, Double currentMoisture, Double threshold) {
        super(source);
        this.plotId = plotId;
        this.sensorId = sensorId;
        this.currentMoisture = currentMoisture;
        this.threshold = threshold;
    }

    public Long getPlotId() {
        return plotId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Double getCurrentMoisture() {
        return currentMoisture;
    }

    public Double getThreshold() {
        return threshold;
    }
}
