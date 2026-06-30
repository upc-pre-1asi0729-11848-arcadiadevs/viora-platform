package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

import java.util.List;

/**
 * Represents an official digital prescription issued by a specialist.
 *
 * @param applicationMethod         the application method
 * @param sprayVolume               the spray volume
 * @param preHarvestInterval        the safety window before harvest
 * @param agronomistRecommendations general agronomist recommendations
 * @param requiredPPE               list of required PPE
 * @param products                  the list of prescribed products
 */
public record AgrochemicalPrescription(
        ApplicationMethod applicationMethod,
        SprayVolume sprayVolume,
        PreHarvestInterval preHarvestInterval,
        String agronomistRecommendations,
        List<PersonalProtectiveEquipment> requiredPPE,
        List<PrescribedProduct> products
) {
    public AgrochemicalPrescription {
        if (applicationMethod == null) {
            throw new IllegalArgumentException("Application method cannot be null");
        }
        if (sprayVolume == null) {
            throw new IllegalArgumentException("Spray volume cannot be null");
        }
        if (preHarvestInterval == null) {
            throw new IllegalArgumentException("Pre-harvest interval must be provided");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("At least one product must be prescribed");
        }
        if (requiredPPE == null) {
            requiredPPE = List.of();
        }
    }
}
