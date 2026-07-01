package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import java.util.Date;

public record SubmitServiceProposalResource(
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
}
