package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

/**
 * Command to clear a plot's grower-declared chill requirement, reverting it to
 * the system default derived from the crop.
 *
 * @param plotId The plot whose requirement override is being cleared.
 * @param userId The owner clearing the requirement.
 */
public record ResetChillRequirementCommand(
        Long plotId,
        Long userId
) {
    public ResetChillRequirementCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }
}
