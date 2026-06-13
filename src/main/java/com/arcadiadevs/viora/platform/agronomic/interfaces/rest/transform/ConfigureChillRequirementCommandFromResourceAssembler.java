package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.ConfigureChillRequirementCommand;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.ConfigureChillRequirementResource;

/**
 * Maps a configure-chill-requirement request into its domain command.
 */
public final class ConfigureChillRequirementCommandFromResourceAssembler {

    private ConfigureChillRequirementCommandFromResourceAssembler() {
    }

    public static ConfigureChillRequirementCommand toCommandFromResource(
            Long plotId,
            Long userId,
            ConfigureChillRequirementResource resource
    ) {
        return new ConfigureChillRequirementCommand(
                plotId,
                userId,
                resource.chillRequirementPortions()
        );
    }
}
