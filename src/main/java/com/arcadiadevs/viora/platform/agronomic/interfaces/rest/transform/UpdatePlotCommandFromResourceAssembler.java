package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdatePlotCommand;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.UpdatePlotResource;

/**
 * Assembler to convert UpdatePlotResource into UpdatePlotCommand.
 */
public class UpdatePlotCommandFromResourceAssembler {

    /**
     * Converts an UpdatePlotResource into an UpdatePlotCommand.
     *
     * @param plotId The plot identifier from the path variable.
     * @param resource The request body resource.
     * @return The UpdatePlotCommand.
     */
    public static UpdatePlotCommand toCommandFromResource(Long plotId, UpdatePlotResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Update plot request body is required.");
        }

        return new UpdatePlotCommand(
                plotId,
                resource.name(),
                resource.polygonCoordinates(),
                resource.areaSizeHectares(),
                resource.cropType(),
                resource.variety()
        );
    }
}