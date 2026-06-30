package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.ServiceProposal;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.AcceptServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.RejectServiceProposalCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SubmitServiceProposalCommand;

import java.util.Optional;

/**
 * Command Service for managing Service Proposals.
 * Provides operations to submit, accept, and reject proposals.
 */
public interface ServiceProposalCommandService {
    
    /**
     * Handles the submission of a new service proposal.
     *
     * @param command the command containing proposal details
     * @return the created service proposal, or empty if it fails
     */
    Optional<ServiceProposal> handle(SubmitServiceProposalCommand command);

    /**
     * Handles the acceptance of an existing service proposal.
     *
     * @param command the command containing the proposal ID
     * @return the updated service proposal, or empty if not found
     */
    Optional<ServiceProposal> handle(AcceptServiceProposalCommand command);

    /**
     * Handles the rejection of an existing service proposal.
     *
     * @param command the command containing the proposal ID
     * @return the updated service proposal, or empty if not found
     */
    Optional<ServiceProposal> handle(RejectServiceProposalCommand command);
}
