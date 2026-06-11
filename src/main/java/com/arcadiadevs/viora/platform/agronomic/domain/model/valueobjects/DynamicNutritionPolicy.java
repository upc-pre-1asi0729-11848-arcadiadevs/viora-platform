package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Configurable agronomic policy used to classify risk and build nutrition plans.
 *
 * @param temperatureReferenceCelsius Configured temperature reference for anomaly calculation.
 * @param highRiskNdviThreshold NDVI value below which vegetation risk is high.
 * @param moderateRiskNdviThreshold NDVI value below which vegetation risk is moderate.
 * @param highRiskWindowDays Application window for high risk.
 * @param extremeRiskWindowDays Application window for extreme risk.
 * @param foliarSupportDosageLitersPerHectare Foliar support dosage.
 * @param potassiumCalciumDosageKilogramsPerHectare Potassium-calcium dosage.
 * @param biostimulantDosageLitersPerHectare Biostimulant dosage.
 */
public record DynamicNutritionPolicy(
        double temperatureReferenceCelsius,
        double highRiskNdviThreshold,
        double moderateRiskNdviThreshold,
        int highRiskWindowDays,
        int extremeRiskWindowDays,
        double foliarSupportDosageLitersPerHectare,
        double potassiumCalciumDosageKilogramsPerHectare,
        double biostimulantDosageLitersPerHectare
) {
    public DynamicNutritionPolicy {
        if (!Double.isFinite(temperatureReferenceCelsius)) {
            throw new IllegalArgumentException("Temperature reference must be finite.");
        }
        if (highRiskNdviThreshold < -1.0 || highRiskNdviThreshold > 1.0) {
            throw new IllegalArgumentException("High-risk NDVI threshold must be between -1 and 1.");
        }
        if (moderateRiskNdviThreshold < -1.0 || moderateRiskNdviThreshold > 1.0) {
            throw new IllegalArgumentException("Moderate-risk NDVI threshold must be between -1 and 1.");
        }
        if (highRiskNdviThreshold >= moderateRiskNdviThreshold) {
            throw new IllegalArgumentException(
                    "High-risk NDVI threshold must be lower than the moderate-risk threshold."
            );
        }
        if (highRiskWindowDays < 1 || extremeRiskWindowDays < 1) {
            throw new IllegalArgumentException("Application windows must contain at least one day.");
        }
        if (foliarSupportDosageLitersPerHectare <= 0
                || potassiumCalciumDosageKilogramsPerHectare <= 0
                || biostimulantDosageLitersPerHectare <= 0) {
            throw new IllegalArgumentException("Nutrition dosages must be positive.");
        }
    }
}
