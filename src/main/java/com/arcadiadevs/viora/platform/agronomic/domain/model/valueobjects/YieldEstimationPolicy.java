package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Parameters for the transparent yield-estimation heuristic.
 *
 * <p>
 * These are course-level defaults that approximate yield from vegetation vigor
 * (NDVI) and chill adequacy scaled by plot area. They are an explainable
 * estimate, not a calibrated agronomic prediction, and must be validated by an
 * agronomist for the target crop before being used for real decisions.
 * </p>
 *
 * <p>
 * The chill requirement is not part of this policy: it is a per-plot, crop-derived
 * value resolved separately (see {@code ChillRequirementResolver}) and passed into
 * the estimator, so the chart reference line and the yield modifier share a single
 * requirement.
 * </p>
 *
 * @param baseYieldTonnesPerHectare Potential yield per hectare under optimal vigor and chill.
 * @param ndviFloor NDVI at or below which vigor contributes no yield.
 * @param ndviOptimal NDVI at or above which vigor contribution is maximal.
 * @param chillMinFactor Minimum chill modifier applied when chill is fully inadequate.
 */
public record YieldEstimationPolicy(
        double baseYieldTonnesPerHectare,
        double ndviFloor,
        double ndviOptimal,
        double chillMinFactor
) {
    public YieldEstimationPolicy {
        if (!(baseYieldTonnesPerHectare > 0)) {
            throw new IllegalArgumentException("Base yield per hectare must be positive.");
        }
        if (ndviFloor < -1.0 || ndviFloor > 1.0) {
            throw new IllegalArgumentException("NDVI floor must be between -1 and 1.");
        }
        if (ndviOptimal <= ndviFloor || ndviOptimal > 1.0) {
            throw new IllegalArgumentException("NDVI optimal must be greater than the floor and at most 1.");
        }
        if (chillMinFactor < 0.0 || chillMinFactor > 1.0) {
            throw new IllegalArgumentException("Chill minimum factor must be between 0 and 1.");
        }
    }
}
