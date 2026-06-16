package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.PestSightingReportResource;

public class PestSightingReportResourceFromPestSightingReportAssembler {
    public static PestSightingReportResource toResourceFromAggregate(PestSightingReport aggregate) {
        return new PestSightingReportResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getPlotId().value(),
                aggregate.getReporterUserId().value(),
                aggregate.getRiskZone().name(),
                aggregate.getSymptoms().getDescriptions(),
                aggregate.getObservedSeverity().name(),
                aggregate.getNotes(),
                aggregate.isEvaluated(),
                aggregate.getCalculatedRisk() != null ? aggregate.getCalculatedRisk().name() : null,
                aggregate.getProbableThreat() != null ? aggregate.getProbableThreat().name() : null,
                aggregate.getStatus() != null ? aggregate.getStatus().name() : null,
                aggregate.isAlertConfirmed()
        );
    }
}
