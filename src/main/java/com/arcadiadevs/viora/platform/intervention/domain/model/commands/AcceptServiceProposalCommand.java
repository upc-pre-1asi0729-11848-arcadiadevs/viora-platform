package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

/**
 * Command to accept an existing service proposal.
 *
 * @param serviceProposalId the ID of the service proposal to accept
 */
public record AcceptServiceProposalCommand(Long serviceProposalId) {
    public AcceptServiceProposalCommand {
        if (serviceProposalId == null || serviceProposalId <= 0) {
            throw new IllegalArgumentException("Service proposal ID must be provided and positive");
        }
    }
}
