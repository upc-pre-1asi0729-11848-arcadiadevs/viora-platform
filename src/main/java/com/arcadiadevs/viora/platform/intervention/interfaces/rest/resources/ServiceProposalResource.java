package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

import java.util.Date;

public record ServiceProposalResource(
        Long id,
        Long interventionRequestId,
        Long specialistId,
        Date proposedDate,
        Double amount,
        String currency,
        String proposalDetails,
        String status
) {
}
