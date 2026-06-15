package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreatePestSightingReportCommand;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.CreatePestSightingReportResource;

public class CreatePestSightingReportCommandFromResourceAssembler {
    public static CreatePestSightingReportCommand toCommandFromResource(CreatePestSightingReportResource resource) {
        return new CreatePestSightingReportCommand(
                resource.plotId(),
                resource.reporterUserId(),
                resource.riskZone(),
                resource.symptoms(),
                resource.observedSeverity(),
                resource.notes()
        );
    }
}
