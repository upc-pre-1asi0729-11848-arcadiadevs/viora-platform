package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * The agronomic model used to quantify accumulated winter chill.
 *
 * <p>
 * Viora quantifies chill accumulation, the chill requirement, the trend chart
 * and the yield-forecast chill modifier with a single, explicit model so the
 * unit is never ambiguous. The platform standard is the {@link #DYNAMIC} model
 * (Fishman &amp; Erez), whose unit is the Chill Portion (CP); it is the most
 * reliable model in warm climates such as the olive-growing valleys Viora
 * targets, where the simpler Chilling Hours and Utah models break down.
 * </p>
 *
 * <p>
 * {@link #CHILLING_HOURS} and {@link #UTAH} are recognized vocabulary kept for
 * provenance and migration; they are not produced by the current accumulator.
 * </p>
 */
public enum ChillMetricModel {

    /** Hours within the [0, 7.2] °C range. Unit: chill hours (CH). */
    CHILLING_HOURS("Chilling Hours", "CH"),

    /** Utah chill-unit weighting (credits moderate cold, penalizes warm hours). Unit: chill units (CU). */
    UTAH("Utah", "CU"),

    /** Fishman–Erez Dynamic Model. Unit: chill portions (CP). The Viora standard. */
    DYNAMIC("Dynamic Model", "CP");

    private final String displayName;
    private final String unitLabel;

    ChillMetricModel(String displayName, String unitLabel) {
        this.displayName = displayName;
        this.unitLabel = unitLabel;
    }

    public String displayName() {
        return displayName;
    }

    public String unitLabel() {
        return unitLabel;
    }
}
