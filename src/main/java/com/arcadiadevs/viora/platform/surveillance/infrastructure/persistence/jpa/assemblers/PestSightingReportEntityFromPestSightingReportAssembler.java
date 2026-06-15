package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.PestSightingReportEntity;

public class PestSightingReportEntityFromPestSightingReportAssembler {

    public static PestSightingReportEntity toEntityFromAggregate(PestSightingReport aggregate) {
        var entity = new PestSightingReportEntity();
        
        if (aggregate.getId() != null) {
            entity.setId(aggregate.getId().value());
        }
        
        entity.setPlotId(aggregate.getPlotId().value());
        entity.setReporterUserId(aggregate.getReporterUserId().value());
        entity.setRiskZone(aggregate.getRiskZone() != null ? aggregate.getRiskZone().name() : null);
        
        // Convert list of symptoms to comma separated string (or JSON in a real app)
        if (aggregate.getSymptoms() != null) {
            entity.setSymptoms(String.join(",", aggregate.getSymptoms().getDescriptions()));
        }
        
        entity.setObservedSeverity(aggregate.getObservedSeverity() != null ? aggregate.getObservedSeverity().name() : null);
        entity.setNotes(aggregate.getNotes());
        entity.setEvaluated(aggregate.isEvaluated());
        entity.setCalculatedRisk(aggregate.getCalculatedRisk() != null ? aggregate.getCalculatedRisk().name() : null);
        entity.setProbableThreat(aggregate.getProbableThreat() != null ? aggregate.getProbableThreat().name() : null);
        
        return entity;
    }
}
