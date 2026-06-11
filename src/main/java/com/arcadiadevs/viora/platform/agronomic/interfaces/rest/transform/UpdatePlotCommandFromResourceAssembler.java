package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.UpdatePlotResource;

import java.util.Objects;

/**
 * Assembler to convert UpdatePlotResource into UpdatePlotCommand.
 */
public final class UpdatePlotCommandFromResourceAssembler {

    private UpdatePlotCommandFromResourceAssembler() {
    }

    /**
     * Converts an UpdatePlotResource into an UpdatePlotCommand.
     *
     * @param plotId The plot identifier from the path variable.
     * @param resource The request body resource.
     * @return The UpdatePlotCommand.
     */
    public static UpdatePlotCommand toCommandFromResource(Long plotId, UpdatePlotResource resource) {
        Objects.requireNonNull(resource, "Update plot request body is required.");

        return new UpdatePlotCommand(
                plotId,
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
