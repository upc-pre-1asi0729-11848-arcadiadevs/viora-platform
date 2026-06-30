package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

/**
 * Command to initialize a new Treatment Prescription linked to a Service Proposal.
 *
 * @param serviceProposalId the accepted service proposal ID
 */
public record CreateTreatmentPrescriptionCommand(Long serviceProposalId) {
    public CreateTreatmentPrescriptionCommand {
        if (serviceProposalId == null || serviceProposalId <= 0) {
            throw new IllegalArgumentException("Service proposal ID must be provided and positive");
        }
    }
}
