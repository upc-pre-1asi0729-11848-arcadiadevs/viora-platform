package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Resource representing the active alert summary.
 */
public record ActiveAlertResource(
        Long id,
        String title,
        String location,
        String area
) {
}
