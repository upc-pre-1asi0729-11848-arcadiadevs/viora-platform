package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request body to declare a plot's winter-chill requirement from the plot's
 * agronomic configuration.
 *
 * @param chillRequirementPortions The declared requirement, in Dynamic Model chill portions (CP).
 */
public record ConfigureChillRequirementResource(
        @NotNull(message = "Chill requirement portions are required.")
        @Positive(message = "Chill requirement portions must be positive.")
        Double chillRequirementPortions
) {
}
