package com.arcadiadevs.viora.platform.intervention.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CloseInterventionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.ReportInterventionImpactCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.GracePeriod;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.CloseInterventionResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionOutcomeResource;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.ReportInterventionImpactResource;

public class InterventionOutcomeResourceAssembler {

    public static ReportInterventionImpactCommand toCommandFromResource(ReportInterventionImpactResource resource) {
        return new ReportInterventionImpactCommand(
                resource.interventionExecutionId(),
                new GracePeriod(resource.gracePeriod()),
                ObservedResult.valueOf(resource.observedResult()),
                ImpactLevel.valueOf(resource.impactLevel()),
                resource.producerAssessment()
        );
    }

    public static CloseInterventionCommand toCommandFromResource(Long outcomeId, CloseInterventionResource resource) {
        return new CloseInterventionCommand(
                outcomeId,
                ServiceResult.valueOf(resource.serviceResult()),
                HireAgain.valueOf(resource.hireAgain()),
                resource.privateFeedback()
        );
    }

    public static InterventionOutcomeResource toResourceFromDomain(InterventionOutcome domain) {
        return new InterventionOutcomeResource(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getInterventionExecutionId().value(),
                domain.getImpactReport() != null ? domain.getImpactReport().gracePeriod().description() : null,
                domain.getImpactReport() != null && domain.getImpactReport().observedResult() != null ? domain.getImpactReport().observedResult().name() : null,
                domain.getImpactReport() != null && domain.getImpactReport().impactLevel() != null ? domain.getImpactReport().impactLevel().name() : null,
                domain.getImpactReport() != null ? domain.getImpactReport().producerAssessment() : null,
                domain.getServiceEvaluation() != null && domain.getServiceEvaluation().serviceResult() != null ? domain.getServiceEvaluation().serviceResult().name() : null,
                domain.getServiceEvaluation() != null && domain.getServiceEvaluation().hireAgain() != null ? domain.getServiceEvaluation().hireAgain().name() : null,
                domain.getServiceEvaluation() != null ? domain.getServiceEvaluation().privateFeedback() : null,
                domain.getStatus() != null ? domain.getStatus().name() : null
        );
    }
}
