package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

/**
 * Command to reject an existing service proposal.
 *
 * @param serviceProposalId the ID of the service proposal to reject
 */
public record RejectServiceProposalCommand(Long serviceProposalId) {
    public RejectServiceProposalCommand {
        if (serviceProposalId == null || serviceProposalId <= 0) {
            throw new IllegalArgumentException("Service proposal ID must be provided and positive");
        }
    }
}
