package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

/**
 * REST projection of a plot's effective winter-chill requirement.
 *
 * @param chillRequirementPortions The requirement value in the model's unit.
 * @param source Provenance of the value (e.g. SYSTEM_DEFAULT, USER_DECLARED).
 * @param model The chill model the value is expressed in (e.g. DYNAMIC).
 * @param unit The unit label of the model (e.g. CP).
 */
public record ChillRequirementResource(
        Double chillRequirementPortions,
        String source,
        String model,
        String unit
) {
}
