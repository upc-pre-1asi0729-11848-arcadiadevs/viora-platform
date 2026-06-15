package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreatePestSightingReportResource(
        @NotNull Long plotId,
        @NotNull Long reporterUserId,
        @NotBlank String riskZone,
        @NotEmpty List<String> symptoms,
        @NotBlank String observedSeverity,
        String notes
) {
}
