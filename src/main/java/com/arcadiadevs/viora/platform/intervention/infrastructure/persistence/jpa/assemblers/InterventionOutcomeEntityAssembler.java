package com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.assemblers;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.GracePeriod;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ImpactReport;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionOutcomeId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceEvaluation;
import com.arcadiadevs.viora.platform.intervention.infrastructure.persistence.jpa.entities.InterventionOutcomeEntity;

public class InterventionOutcomeEntityAssembler {

    public static InterventionOutcome toDomain(InterventionOutcomeEntity entity) {
        if (entity == null) {
            return null;
        }

        ImpactReport impactReport = null;
        if (entity.getGracePeriod() != null) {
            impactReport = new ImpactReport(
                    new GracePeriod(entity.getGracePeriod()),
                    entity.getObservedResult(),
                    entity.getImpactLevel(),
                    entity.getProducerAssessment()
            );
        }

        ServiceEvaluation serviceEvaluation = null;
        if (entity.getServiceResult() != null) {
            serviceEvaluation = new ServiceEvaluation(
                    entity.getServiceResult(),
                    entity.getHireAgain(),
                    entity.getPrivateFeedback()
            );
        }

        var domain = new InterventionOutcome(
                entity.getInterventionExecutionId(),
                impactReport,
                serviceEvaluation,
                entity.getStatus()
        );
        domain.restoreIdentity(new InterventionOutcomeId(entity.getId()));
        return domain;
    }

    public static InterventionOutcomeEntity toEntity(InterventionOutcome domain) {
        if (domain == null) {
            return null;
        }

        var entity = new InterventionOutcomeEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().value());
        }
        entity.setInterventionExecutionId(domain.getInterventionExecutionId());
        entity.setStatus(domain.getStatus());

        if (domain.getImpactReport() != null) {
            entity.setGracePeriod(domain.getImpactReport().gracePeriod().description());
            entity.setObservedResult(domain.getImpactReport().observedResult());
            entity.setImpactLevel(domain.getImpactReport().impactLevel());
            entity.setProducerAssessment(domain.getImpactReport().producerAssessment());
        }

        if (domain.getServiceEvaluation() != null) {
            entity.setServiceResult(domain.getServiceEvaluation().serviceResult());
            entity.setHireAgain(domain.getServiceEvaluation().hireAgain());
            entity.setPrivateFeedback(domain.getServiceEvaluation().privateFeedback());
        }

        return entity;
    }
}
