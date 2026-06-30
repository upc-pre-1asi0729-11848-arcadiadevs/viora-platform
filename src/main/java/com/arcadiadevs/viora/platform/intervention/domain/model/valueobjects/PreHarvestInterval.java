package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record PreHarvestInterval(Integer days) {
    public PreHarvestInterval {
        if (days == null || days < 0) {
            throw new IllegalArgumentException("Pre-harvest interval days must be non-negative");
        }
    }
}
