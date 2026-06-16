package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import org.jspecify.annotations.NullMarked;

/**
 * A symptom dictionary item representing an observable biological issue.
 *
 * @param id          Unique identifier / translation key of the symptom
 * @param description Human readable description of the symptom
 */
@NullMarked
public record SymptomResource(
        String id,
        String description
) {}
