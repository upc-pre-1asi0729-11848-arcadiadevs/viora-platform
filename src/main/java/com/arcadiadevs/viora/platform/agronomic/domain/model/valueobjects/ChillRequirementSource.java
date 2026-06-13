package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Provenance of a plot's chill requirement, expressing how much the value can
 * be trusted.
 *
 * <p>
 * The chill requirement is an agronomic property of the crop and variety, not a
 * user preference; but the platform cannot know the exact cultivar requirement
 * on its own, so the value carries the source that produced it. This lets the
 * UI present a system-derived default transparently while leaving room for a
 * grower-declared or agronomist-validated value to override it later.
 * </p>
 *
 * <p>
 * The current system only produces {@link #SYSTEM_DEFAULT} (when the crop maps
 * to a known default) or {@link #NOT_CONFIGURED} (otherwise).
 * {@link #USER_DECLARED} and {@link #AGRONOMIST_VALIDATED} are reserved for the
 * per-plot agronomic configuration capability.
 * </p>
 */
public enum ChillRequirementSource {

    /** No crop-specific requirement is known; a neutral placeholder value is used. */
    NOT_CONFIGURED,

    /** Derived from the platform's built-in per-crop default table (low confidence). */
    SYSTEM_DEFAULT,

    /** Declared by the grower for this plot. */
    USER_DECLARED,

    /** Confirmed by an agronomist for this plot (highest confidence). */
    AGRONOMIST_VALIDATED
}
