package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource for updating the status of a service proposal.
 *
 * @param status the new status (e.g., ACCEPTED, REJECTED)
 * @param reason the reason for the status change, if applicable
 */
public record UpdateServiceProposalStatusResource(String status, String reason) {
}
