package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for updating an Alert.
 */
public record UpdateAlertResource(
        @Schema(description = "The new status to apply to the alert, e.g., UNDER_REVIEW")
        String status
) {
}
