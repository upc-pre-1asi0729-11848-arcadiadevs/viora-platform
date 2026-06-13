package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Course-level defaults for resolving a crop's chill requirement.
 *
 * <p>
 * Holds a neutral placeholder requirement plus a per-crop default table, all
 * expressed as Dynamic Model chill portions. These are transparent approximations
 * that must be validated by an agronomist before driving production decisions;
 * the resulting {@link ChillRequirement} always carries its
 * {@link ChillRequirementSource} so callers never mistake a default for a
 * validated figure.
 * </p>
 *
 * @param defaultRequirementPortions Neutral requirement used when the crop is unknown.
 * @param cropRequirementPortions Per-crop defaults keyed by lower-cased crop name.
 */
public record ChillRequirementPolicy(
        double defaultRequirementPortions,
        Map<String, Double> cropRequirementPortions
) {
    public ChillRequirementPolicy {
        if (!(defaultRequirementPortions > 0)) {
            throw new IllegalArgumentException("Default chill requirement must be positive.");
        }
        cropRequirementPortions = normalize(cropRequirementPortions);
    }

    /**
     * Resolves the requirement for a crop, tagging its provenance.
     *
     * @param cropType The plot's crop type (free text), may be null or blank.
     * @return A {@link ChillRequirement} sourced from the crop table when known,
     *         or a {@code NOT_CONFIGURED} placeholder otherwise.
     */
    public ChillRequirement resolveFor(String cropType) {
        String key = cropType == null ? "" : cropType.trim().toLowerCase(Locale.ROOT);
        Double cropValue = key.isEmpty() ? null : cropRequirementPortions.get(key);
        if (cropValue != null) {
            return new ChillRequirement(
                    new ChillPortions(cropValue),
                    ChillRequirementSource.SYSTEM_DEFAULT,
                    ChillMetricModel.DYNAMIC
            );
        }
        return new ChillRequirement(
                new ChillPortions(defaultRequirementPortions),
                ChillRequirementSource.NOT_CONFIGURED,
                ChillMetricModel.DYNAMIC
        );
    }

    private static Map<String, Double> normalize(Map<String, Double> raw) {
        if (raw == null || raw.isEmpty()) {
            return Map.of();
        }
        return raw.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
                .collect(Collectors.toUnmodifiableMap(
                        entry -> entry.getKey().trim().toLowerCase(Locale.ROOT),
                        Map.Entry::getValue,
                        (first, second) -> first
                ));
    }
}
