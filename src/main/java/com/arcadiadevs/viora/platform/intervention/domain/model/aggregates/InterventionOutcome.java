package com.arcadiadevs.viora.platform.intervention.domain.model.aggregates;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CloseInterventionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.ReportInterventionImpactCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ImpactReport;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionExecutionId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionOutcomeId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.OutcomeStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceEvaluation;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Aggregate Root representing the final outcome of an intervention.
 */
@Getter
public class InterventionOutcome extends AbstractDomainAggregateRoot<InterventionOutcome> {

    private InterventionOutcomeId id;
    private InterventionExecutionId interventionExecutionId;
    private ImpactReport impactReport;
    private ServiceEvaluation serviceEvaluation;
    private OutcomeStatus status;

    protected InterventionOutcome() {
        // Required by JPA
    }

    public InterventionOutcome(ReportInterventionImpactCommand command) {
        this.interventionExecutionId = new InterventionExecutionId(command.interventionExecutionId());
        this.impactReport = new ImpactReport(
                command.gracePeriod(),
                command.observedResult(),
                command.impactLevel(),
                command.producerAssessment()
        );
        this.status = OutcomeStatus.IMPACT_REPORTED;
    }

    public InterventionOutcome(InterventionExecutionId executionId, ImpactReport impactReport, ServiceEvaluation serviceEvaluation, OutcomeStatus status) {
        this.interventionExecutionId = executionId;
        this.impactReport = impactReport;
        this.serviceEvaluation = serviceEvaluation;
        this.status = status;
    }

    public void close(CloseInterventionCommand command) {
        if (this.status == OutcomeStatus.CLOSED) {
            throw new IllegalStateException("Intervention is already closed");
        }
        
        this.serviceEvaluation = new ServiceEvaluation(
                command.serviceResult(),
                command.hireAgain(),
                command.privateFeedback()
        );
        this.status = OutcomeStatus.CLOSED;
    }

    public void restoreIdentity(InterventionOutcomeId id) {
        this.id = id;
    }
}
