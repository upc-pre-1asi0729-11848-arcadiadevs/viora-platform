package com.arcadiadevs.viora.platform.intervention.domain.model.aggregates;

import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SubmitServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.CostEstimate;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.InterventionRequestId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalId;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalStatus;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/**
 * Represents a service proposal in the domain.
 * This aggregate root manages the lifecycle of a proposal submitted by a specialist.
 */
@Getter
public class ServiceProposal extends AbstractDomainAggregateRoot<ServiceProposal> {

    private ServiceProposalId id;
    private InterventionRequestId interventionRequestId;
    private Long specialistId;
    private String serviceTitle;
    private String durationLabel;
    private List<String> scope;
    private Date proposedDate;
    private CostEstimate costEstimate;
    private String proposalDetails;
    private ServiceProposalStatus status;

    /**
     * Default constructor required by JPA.
     */
    public ServiceProposal() {
    }

    /**
     * Constructs a ServiceProposal from a SubmitServiceProposalCommand.
     *
     * @param command the command containing the proposal details
     */
    public ServiceProposal(SubmitServiceProposalCommand command) {
        this.interventionRequestId = new InterventionRequestId(command.interventionRequestId());
        this.specialistId = command.specialistId();
        this.serviceTitle = command.serviceTitle();
        this.durationLabel = command.durationLabel();
        this.scope = command.scope();
        this.proposedDate = command.proposedDate();
        this.costEstimate = new CostEstimate(command.amount(), command.currency());
        this.proposalDetails = command.proposalDetails();
        this.status = ServiceProposalStatus.PENDING;
    }

    /**
     * Constructs a ServiceProposal for restoration from infrastructure.
     *
     * @param interventionRequestId the intervention request ID
     * @param specialistId          the specialist ID
     * @param proposedDate          the proposed date
     * @param amount                the cost amount
     * @param currency              the cost currency
     * @param proposalDetails       the proposal details
     */
    public ServiceProposal(InterventionRequestId interventionRequestId, Long specialistId, Date proposedDate, Double amount, String currency, String proposalDetails) {
        this.interventionRequestId = interventionRequestId;
        this.specialistId = specialistId;
        this.proposedDate = proposedDate;
        this.costEstimate = new CostEstimate(amount, currency);
        this.proposalDetails = proposalDetails;
        this.status = ServiceProposalStatus.PENDING;
    }

    public void restoreIdentity(ServiceProposalId id) {
        this.id = id;
    }

    public void restoreStatus(ServiceProposalStatus status) {
        this.status = status;
    }

    /** Restores the structured proposal content when rebuilding from storage. */
    public void restoreDetails(String serviceTitle, String durationLabel, List<String> scope) {
        this.serviceTitle = serviceTitle;
        this.durationLabel = durationLabel;
        this.scope = scope;
    }

    /**
     * Accepts the proposal.
     *
     * @throws IllegalStateException if the proposal is not in PENDING state
     */
    public void accept() {
        if (this.status != ServiceProposalStatus.PENDING) {
            throw new IllegalStateException("Only pending proposals can be accepted");
        }
        this.status = ServiceProposalStatus.ACCEPTED;
    }

    /**
     * Rejects the proposal.
     *
     * @throws IllegalStateException if the proposal is not in PENDING state
     */
    public void reject() {
        if (this.status != ServiceProposalStatus.PENDING) {
            throw new IllegalStateException("Only pending proposals can be rejected");
        }
        this.status = ServiceProposalStatus.REJECTED;
    }
}
