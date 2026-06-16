package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

/**
 * Represents a single observed symptom.
 */
public record Symptom(String description) {
    public Symptom {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Symptom description cannot be null or empty");
        }
    }
}
