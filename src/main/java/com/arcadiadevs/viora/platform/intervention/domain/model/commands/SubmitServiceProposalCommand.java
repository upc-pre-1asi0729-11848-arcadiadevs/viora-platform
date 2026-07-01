package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

import java.util.Date;

/**
 * Command to submit a new service proposal.
 *
 * @param interventionRequestId the ID of the intervention request
 * @param specialistId          the ID of the specialist making the proposal
 * @param proposedDate          the proposed date for the service
 * @param amount                the cost amount
 * @param currency              the currency of the cost
 * @param proposalDetails       the details of the proposal
 */
public record SubmitServiceProposalCommand(
        Long interventionRequestId,
        Long specialistId,
        String serviceTitle,
        String durationLabel,
        java.util.List<String> scope,
        Date proposedDate,
        Double amount,
        String currency,
        String proposalDetails
) {
    public SubmitServiceProposalCommand {
        if (interventionRequestId == null || interventionRequestId <= 0) {
            throw new IllegalArgumentException("Intervention request ID must be provided and positive");
        }
        if (specialistId == null || specialistId <= 0) {
            throw new IllegalArgumentException("Specialist ID must be provided and positive");
        }
        if (proposedDate == null) {
            throw new IllegalArgumentException("Proposed date must be provided");
        }
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must be provided");
        }
    }
}
