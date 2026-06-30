package com.arcadiadevs.viora.platform.intervention.domain.model.commands;

import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationMethod;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PersonalProtectiveEquipment;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PreHarvestInterval;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PrescribedProduct;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SprayVolume;

import java.util.List;

/**
 * Command to prescribe an agrochemical treatment.
 *
 * @param treatmentPrescriptionId   the ID of the treatment prescription
 * @param applicationMethod         the application method
 * @param sprayVolume               the spray volume
 * @param preHarvestInterval        days before harvest
 * @param agronomistRecommendations general recommendations
 * @param requiredPPE               list of required PPE
 * @param products                  the products to prescribe
 */
public record PrescribeTreatmentCommand(
        Long treatmentPrescriptionId,
        ApplicationMethod applicationMethod,
        SprayVolume sprayVolume,
        PreHarvestInterval preHarvestInterval,
        String agronomistRecommendations,
        List<PersonalProtectiveEquipment> requiredPPE,
        List<PrescribedProduct> products
) {
    public PrescribeTreatmentCommand {
        if (treatmentPrescriptionId == null || treatmentPrescriptionId <= 0) {
            throw new IllegalArgumentException("Treatment prescription ID must be provided and positive");
        }
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
    }
}
