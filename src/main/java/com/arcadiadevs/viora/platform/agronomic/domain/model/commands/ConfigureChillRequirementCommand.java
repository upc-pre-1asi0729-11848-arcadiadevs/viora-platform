package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

/**
 * Command to set a plot's grower-declared winter-chill requirement.
 *
 * <p>
 * The chill requirement is an agronomic property of the crop and variety that
 * the platform cannot infer on its own. This command lets the plot owner declare
 * it from the plot's agronomic configuration; the resulting value is tagged as
 * {@code USER_DECLARED} provenance, overriding the system default.
 * </p>
 *
 * @param plotId The plot whose requirement is being configured.
 * @param userId The owner declaring the requirement.
 * @param chillRequirementPortions The declared requirement, in Dynamic Model chill portions.
 */
public record ConfigureChillRequirementCommand(
        Long plotId,
        Long userId,
        Double chillRequirementPortions
) {
    public ConfigureChillRequirementCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (chillRequirementPortions == null) {
            throw new IllegalArgumentException("Chill requirement portions are required.");
        }
    }
}
