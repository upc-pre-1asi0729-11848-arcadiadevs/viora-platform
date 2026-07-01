package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.InterventionSummaryQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionOutcome;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionRequest;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetGrowerInterventionsQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.OutcomeStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionStatus;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionExecutionRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionOutcomeRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionRequestRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.ServiceProposalRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.TreatmentPrescriptionRepository;
import com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources.InterventionSummaryResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterventionSummaryQueryServiceImpl implements InterventionSummaryQueryService {

    private final InterventionRequestRepository interventionRequestRepository;
    private final ServiceProposalRepository serviceProposalRepository;
    private final TreatmentPrescriptionRepository treatmentPrescriptionRepository;
    private final InterventionExecutionRepository interventionExecutionRepository;
    private final InterventionOutcomeRepository interventionOutcomeRepository;

    public InterventionSummaryQueryServiceImpl(
            InterventionRequestRepository interventionRequestRepository,
            ServiceProposalRepository serviceProposalRepository,
            TreatmentPrescriptionRepository treatmentPrescriptionRepository,
            InterventionExecutionRepository interventionExecutionRepository,
            InterventionOutcomeRepository interventionOutcomeRepository) {
        this.interventionRequestRepository = interventionRequestRepository;
        this.serviceProposalRepository = serviceProposalRepository;
        this.treatmentPrescriptionRepository = treatmentPrescriptionRepository;
        this.interventionExecutionRepository = interventionExecutionRepository;
        this.interventionOutcomeRepository = interventionOutcomeRepository;
    }

    @Override
    public List<InterventionSummaryResource> handle(GetGrowerInterventionsQuery query) {
        var interventions = new ArrayList<InterventionSummaryResource>();

        for (var request : interventionRequestRepository.findByGrowerId(query.growerId())) {
            // An intervention starts only once the producer accepted a proposal.
            if (request.getStatus() != InterventionStatus.ACCEPTED) {
                continue;
            }

            var proposal = serviceProposalRepository
                    .findByInterventionRequestId(request.getId().value()).stream()
                    .filter(candidate -> candidate.getStatus() == ServiceProposalStatus.ACCEPTED)
                    .findFirst()
                    .orElse(null);
            if (proposal == null) {
                continue;
            }

            var prescription = treatmentPrescriptionRepository
                    .findByServiceProposalId(proposal.getId().value())
                    .orElse(null);

            InterventionExecution execution = null;
            InterventionOutcome outcome = null;
            if (prescription != null) {
                execution = interventionExecutionRepository
                        .findByTreatmentPrescriptionId(prescription.getId())
                        .orElse(null);
                if (execution != null) {
                    outcome = interventionOutcomeRepository
                            .findByInterventionExecutionId(execution.getId())
                            .orElse(null);
                }
            }

            interventions.add(toResource(request, proposal, prescription, execution, outcome));
        }

        return interventions;
    }

    private InterventionSummaryResource toResource(
            InterventionRequest request,
            ServiceProposal proposal,
            TreatmentPrescription prescription,
            InterventionExecution execution,
            InterventionOutcome outcome) {

        var cost = proposal.getCostEstimate();
        return new InterventionSummaryResource(
                "INT-%03d".formatted(request.getId().value()),
                request.getId().value(),
                request.getReferenceCode() != null ? request.getReferenceCode().code() : null,
                request.getPlotId(),
                request.getAlertId(),
                request.getSpecialistId(),
                proposal.getId().value(),
                prescription != null ? prescription.getId().value() : null,
                execution != null ? execution.getId().value() : null,
                outcome != null ? outcome.getId().value() : null,
                deriveStatus(prescription, execution, outcome),
                proposal.getServiceTitle(),
                cost != null ? cost.amount() : null,
                cost != null ? cost.currency() : null
        );
    }

    /** Derives the producer-facing lifecycle status from the composed aggregates. */
    private String deriveStatus(
            TreatmentPrescription prescription,
            InterventionExecution execution,
            InterventionOutcome outcome) {
        if (prescription == null || prescription.getStatus() != TreatmentPrescriptionStatus.PRESCRIBED) {
            return "AWAITING_PRESCRIPTION";
        }
        if (execution == null) {
            return "PRESCRIPTION_ISSUED";
        }
        if (outcome == null) {
            return "RECOVERY_MONITORING";
        }
        return outcome.getStatus() == OutcomeStatus.CLOSED ? "CLOSED" : "READY_TO_CLOSE";
    }
}
