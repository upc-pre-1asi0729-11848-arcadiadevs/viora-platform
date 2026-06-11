package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CreatePlotResource;

import java.util.Objects;

/**
 * Assembler that converts the create plot REST resource into its command.
 */
public final class CreatePlotCommandFromResourceAssembler {

    private CreatePlotCommandFromResourceAssembler() {
    }

    public static CreatePlotCommand toCommandFromResource(CreatePlotResource resource) {
        Objects.requireNonNull(resource, "Create plot request body is required.");

        return new CreatePlotCommand(
                resource.userId(),
                resource.name(),
                resource.polygonCoordinates(),
                resource.areaSizeHectares(),
                resource.cropType(),
                resource.variety(),
                resource.location(),
                resource.campaign(),
                resource.notes()
        );
    }
}
