package com.arcadiadevs.viora.platform.agronomic.interfaces.events;

import org.springframework.context.ApplicationEvent;

public class ChillDeficitIntegrationEvent extends ApplicationEvent {
    
    private final Long plotId;
    private final Double currentChillAccumulation;
    private final Double targetChill;
    private final Double temperatureAnomaly;

    public ChillDeficitIntegrationEvent(Object source, Long plotId, Double currentChillAccumulation, Double targetChill, Double temperatureAnomaly) {
        super(source);
        this.plotId = plotId;
        this.currentChillAccumulation = currentChillAccumulation;
        this.targetChill = targetChill;
        this.temperatureAnomaly = temperatureAnomaly;
    }

    public Long getPlotId() {
        return plotId;
    }

    public Double getCurrentChillAccumulation() {
        return currentChillAccumulation;
    }

    public Double getTargetChill() {
        return targetChill;
    }

    public Double getTemperatureAnomaly() {
        return temperatureAnomaly;
    }
}
