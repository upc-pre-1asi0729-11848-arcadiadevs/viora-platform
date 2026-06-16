package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an immutable collection of observed symptoms.
 */
public record Symptoms(List<Symptom> items) {
    public Symptoms {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Symptoms list cannot be null or empty");
        }
        items = List.copyOf(items); // Ensure immutability
    }

    public static Symptoms fromDescriptions(List<String> descriptions) {
        if (descriptions == null || descriptions.isEmpty()) {
            throw new IllegalArgumentException("Symptom descriptions list cannot be null or empty");
        }
        return new Symptoms(descriptions.stream().map(Symptom::new).collect(Collectors.toList()));
    }
    
    public List<String> getDescriptions() {
        return items.stream().map(Symptom::description).collect(Collectors.toList());
    }
}
