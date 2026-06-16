package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.AlertTimelineRecord;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.AlertTimelineRecordResource;

import java.util.List;
import java.util.stream.Collectors;

public class AlertTimelineRecordResourceFromEntityAssembler {
    public static AlertTimelineRecordResource toResourceFromEntity(AlertTimelineRecord entity) {
        return new AlertTimelineRecordResource(
                entity.getTag(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCreatedAt() != null ? entity.getCreatedAt() : java.time.LocalDateTime.now()
        );
    }

    public static List<AlertTimelineRecordResource> toResourceListFromEntities(List<AlertTimelineRecord> entities) {
        return entities.stream()
                .map(AlertTimelineRecordResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
    }
}
