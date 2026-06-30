package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

/**
 * Represents a single product prescribed within an agrochemical prescription.
 *
 * @param productName            the name of the product (e.g., Systemic Fungicide A-24)
 * @param dosage                 the dosage
 * @param sessions               the number of application sessions
 * @param technicalRecommendation specific handling instructions
 */
public record PrescribedProduct(
        String productName,
        Dosage dosage,
        ApplicationSessions sessions,
        String technicalRecommendation
) {
    public PrescribedProduct {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (dosage == null) {
            throw new IllegalArgumentException("Dosage cannot be null");
        }
        if (sessions == null) {
            throw new IllegalArgumentException("Sessions must be provided");
        }
    }
}
