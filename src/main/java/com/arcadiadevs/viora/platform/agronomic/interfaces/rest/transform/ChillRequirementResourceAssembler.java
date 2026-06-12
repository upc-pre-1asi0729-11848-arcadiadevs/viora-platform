package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.ChillRequirementResource;

/**
 * Maps a {@link ChillRequirement} value object to its REST resource.
 */
public final class ChillRequirementResourceAssembler {

    private ChillRequirementResourceAssembler() {
    }

    public static ChillRequirementResource toResourceFromValueObject(ChillRequirement requirement) {
        return new ChillRequirementResource(
                requirement.value(),
                requirement.source().name(),
                requirement.model().name(),
                requirement.model().unitLabel()
        );
    }
}
