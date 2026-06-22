package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.AlertTimelineRecord;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertEntity;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertTimelineRecordEntity;

import java.util.stream.Collectors;

public class AlertAggregateToEntityAssembler {

    public static AlertEntity toEntity(Alert aggregate) {
        if (aggregate == null) return null;

        var entity = new AlertEntity();
        if (aggregate.getId() != null) {
            entity.setId(aggregate.getId().value());
        }
        
        entity.setPlotId(aggregate.getPlotId().value());
        entity.setReportId(aggregate.getReportId());
        entity.setType(aggregate.getType());
        entity.setSeverity(aggregate.getSeverity());
        entity.setStatus(aggregate.getStatus());
        entity.setTitle(aggregate.getTitle());
        entity.setRiskExplanation(aggregate.getRiskExplanation());

        if (aggregate.getSources() != null) {
            entity.getSources().addAll(aggregate.getSources());
        }
        if (aggregate.getDataProviders() != null) {
            entity.getDataProviders().addAll(aggregate.getDataProviders());
        }
        if (aggregate.getSupportingData() != null) {
            entity.getSupportingData().putAll(aggregate.getSupportingData());
        }

        if (aggregate.getTimeline() != null) {
            var timelineEntities = aggregate.getTimeline().stream()
                    .map(record -> new AlertTimelineRecordEntity(
                            record.getTag(),
                            record.getTitle(),
                            record.getDescription(),
                            entity
                    ))
                    .collect(Collectors.toList());
            entity.getTimeline().addAll(timelineEntities);
        }

        return entity;
    }
}
