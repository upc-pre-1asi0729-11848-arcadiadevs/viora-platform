package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.Objects;

/**
 * A plot's winter-chill requirement: the chill a crop needs to break dormancy,
 * expressed in the same model and unit as the accumulated chill it is compared
 * against.
 *
 * <p>
 * Bundling the value with its {@link ChillRequirementSource provenance} and the
 * {@link ChillMetricModel model} keeps the requirement honest: a single value
 * feeds both the trend chart's reference line and the yield-forecast chill
 * modifier, and the UI can show whether it is a transparent system default or a
 * validated figure.
 * </p>
 *
 * @param portions The chill requirement, in the unit of {@code model}.
 * @param source How the value was obtained.
 * @param model The chill model the value is expressed in.
 */
public record ChillRequirement(
        ChillPortions portions,
        ChillRequirementSource source,
        ChillMetricModel model
) {
    public ChillRequirement {
        Objects.requireNonNull(portions, "Chill requirement portions are required.");
        Objects.requireNonNull(source, "Chill requirement source is required.");
        Objects.requireNonNull(model, "Chill metric model is required.");
        if (!(portions.getValue() > 0)) {
            throw new IllegalArgumentException("Chill requirement must be positive.");
        }
    }

    /** Convenience accessor for the numeric requirement. */
    public double value() {
        return portions.getValue();
    }
}
