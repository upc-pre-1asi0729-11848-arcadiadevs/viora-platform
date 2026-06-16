package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertEntity;

public class AlertEntityToAggregateAssembler {

    public static Alert toAggregate(AlertEntity entity) {
        if (entity == null) return null;

        var aggregate = new Alert(
                new PlotId(entity.getPlotId()),
                entity.getType(),
                entity.getSeverity(),
                entity.getTitle(),
                entity.getRiskExplanation()
        );

        aggregate.restoreIdentity(new AlertId(entity.getId()));
        
        // Use reflection or direct package access if needed, but since fields are mostly read-only, 
        // we might need to recreate the state via public methods or a restoreState method.
        // For simplicity, we will use reflection or add a restoreState method to Alert.
        // Let's add a restoreState to Alert or use its builder/adders.
        
        // Clear the default generated "Alert generated" timeline record from constructor
        aggregate.getTimeline().clear();

        if (entity.getSources() != null) {
            entity.getSources().forEach(aggregate::addSource);
        }
        if (entity.getDataProviders() != null) {
            entity.getDataProviders().forEach(aggregate::addDataProvider);
        }
        if (entity.getSupportingData() != null) {
            entity.getSupportingData().forEach(aggregate::addSupportingData);
        }

        if (entity.getTimeline() != null) {
            entity.getTimeline().forEach(recordEntity -> 
                aggregate.addTimelineRecord(recordEntity.getTag(), recordEntity.getTitle(), recordEntity.getDescription())
            );
        }

        // We need to restore status since it might not be ACTIVE
        try {
            var statusField = Alert.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(aggregate, entity.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore Alert status", e);
        }

        return aggregate;
    }
}
