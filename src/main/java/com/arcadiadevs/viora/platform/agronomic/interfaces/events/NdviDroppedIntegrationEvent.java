package com.arcadiadevs.viora.platform.agronomic.interfaces.events;

import org.springframework.context.ApplicationEvent;

public class NdviDroppedIntegrationEvent extends ApplicationEvent {
    
    private final Long plotId;
    private final Double currentNdvi;
    private final Double historicalAverage;

    public NdviDroppedIntegrationEvent(Object source, Long plotId, Double currentNdvi, Double historicalAverage) {
        super(source);
        this.plotId = plotId;
        this.currentNdvi = currentNdvi;
        this.historicalAverage = historicalAverage;
    }

    public Long getPlotId() {
        return plotId;
    }

    public Double getCurrentNdvi() {
        return currentNdvi;
    }

    public Double getHistoricalAverage() {
        return historicalAverage;
    }
}
