package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a recommendation for nutritional input.
 *
 * <p>
 * In its simplest form it carries a free-text recommendation (used by
 * mitigation recommendations). For dynamic nutrition plans it also carries
 * the agronomic purpose, the dosage to apply and its application status.
 * </p>
 */
@Getter
@EqualsAndHashCode
public class NutritionInputRecommendation {

    private final String value;
    private final String purpose;
    private final Double dosage;
    private final String dosageUnit;
    private final NutritionInputStatus status;

    /**
     * Creates a free-text nutrition input recommendation.
     *
     * @param value The recommendation text.
     */
    public NutritionInputRecommendation(String value) {
        this(value, null, null, null, NutritionInputStatus.RECOMMENDED);
    }

    /**
     * Creates a dosed nutrition input recommendation for a dynamic nutrition plan.
     *
     * @param value The nutrition input name.
     * @param purpose The agronomic purpose of the input.
     * @param dosage The dosage amount to apply.
     * @param dosageUnit The dosage unit (e.g. L/ha, kg/ha).
     * @param status The application status of the input.
     */
    public NutritionInputRecommendation(
            String value,
            String purpose,
            Double dosage,
            String dosageUnit,
            NutritionInputStatus status
    ) {
        validateRequiredFields(value, status);
        validateDosageConsistency(dosage, dosageUnit);

        this.value = value;
        this.purpose = purpose;
        this.dosage = dosage;
        this.dosageUnit = dosageUnit;
        this.status = status;
    }

    private void validateRequiredFields(String value, NutritionInputStatus status) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Nutrition input recommendation cannot be null or empty.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Nutrition input status is required.");
        }
    }

    private void validateDosageConsistency(Double dosage, String dosageUnit) {
        if (dosage == null && dosageUnit == null) {
            return;
        }
        if (dosage == null || dosageUnit == null || dosageUnit.isBlank()) {
            throw new IllegalArgumentException("Dosage and dosage unit must be provided together.");
        }
        if (dosage <= 0) {
            throw new IllegalArgumentException("Dosage must be a positive number.");
        }
    }
}
