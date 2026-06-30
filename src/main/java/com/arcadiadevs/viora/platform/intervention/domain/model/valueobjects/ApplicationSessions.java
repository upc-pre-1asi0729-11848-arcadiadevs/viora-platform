package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record ApplicationSessions(Integer count) {
    public ApplicationSessions {
        if (count == null || count <= 0) {
            throw new IllegalArgumentException("Application sessions must be positive");
        }
    }
}
