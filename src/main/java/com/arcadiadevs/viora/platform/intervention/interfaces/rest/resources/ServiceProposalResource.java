package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import java.util.Date;
import java.util.List;

public record ServiceProposalResource(
        Long id,
        Long interventionRequestId,
        Long specialistId,
        String serviceTitle,
        String durationLabel,
        List<String> scope,
        Date proposedDate,
        Double amount,
        String currency,
        String proposalDetails,
        String status
) {
}
