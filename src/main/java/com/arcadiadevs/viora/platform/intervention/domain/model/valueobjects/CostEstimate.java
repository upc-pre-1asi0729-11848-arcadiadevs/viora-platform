package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

public record CostEstimate(Double amount, String currency) {
    public CostEstimate {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
    }
}
