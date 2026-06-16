package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertTimelineRecordResource;

import java.util.stream.Collectors;

public class AlertResourceFromAggregateAssembler {

    public static AlertResource toResourceFromAggregate(Alert aggregate) {
        if (aggregate == null) return null;

        var timelineResources = aggregate.getTimeline().stream()
                .map(record -> new AlertTimelineRecordResource(
                        record.getTag(),
                        record.getTitle(),
                        record.getDescription(),
                        record.getCreatedAt()
                ))
                .collect(Collectors.toList());

        var sources = aggregate.getSources().stream()
                .map(AlertSource::name)
                .collect(Collectors.toList());

        return new AlertResource(
                aggregate.getId().value(),
                aggregate.getPlotId().value(),
                aggregate.getType().name(),
                aggregate.getSeverity().name(),
                aggregate.getStatus().name(),
                aggregate.getTitle(),
                aggregate.getRiskExplanation(),
                sources,
                aggregate.getDataProviders(),
                aggregate.getSupportingData(),
                timelineResources
        );
    }
}
