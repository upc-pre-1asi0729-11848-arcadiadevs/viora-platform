package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record ServiceProposalId(Long value) {
    public ServiceProposalId {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Service proposal id cannot be null or negative");
        }
    }
}
