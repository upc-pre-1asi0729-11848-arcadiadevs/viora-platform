package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PestSightingReportId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReporterUserId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.RiskZone;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptoms;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.PestSightingReportEntity;

import java.util.Arrays;

public class PestSightingReportFromPestSightingReportEntityAssembler {

    public static PestSightingReport toAggregateFromEntity(PestSightingReportEntity entity) {
        var plotId = new PlotId(entity.getPlotId());
        var reporterUserId = new ReporterUserId(entity.getReporterUserId());
        var riskZone = entity.getRiskZone() != null ? RiskZone.valueOf(entity.getRiskZone()) : null;
        var symptoms = entity.getSymptoms() != null ? Symptoms.fromDescriptions(Arrays.asList(entity.getSymptoms().split(","))) : null;
        var observedSeverity = entity.getObservedSeverity() != null ? AlertSeverity.valueOf(entity.getObservedSeverity()) : null;
        
        var aggregate = PestSightingReport.registerManualReport(
                plotId,
                reporterUserId,
                riskZone,
                symptoms,
                observedSeverity,
                entity.getNotes()
        );
        
        if (entity.isEvaluated()) {
            aggregate.evaluateBiologicalRisk(
                    entity.getCalculatedRisk() != null ? AlertSeverity.valueOf(entity.getCalculatedRisk()) : null,
                    entity.getProbableThreat() != null ? ThreatType.valueOf(entity.getProbableThreat()) : null
            );
        }
        
        aggregate.restoreIdentity(new PestSightingReportId(entity.getId()));
        
        return aggregate;
    }
}
