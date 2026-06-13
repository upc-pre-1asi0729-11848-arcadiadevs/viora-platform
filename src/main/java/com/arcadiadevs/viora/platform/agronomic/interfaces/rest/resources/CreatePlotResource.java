package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request resource used to register a productive agricultural plot.
 */
public record CreatePlotResource(
        @NotNull @Positive Long userId,
        @NotBlank @Size(min = 3, max = 80) String name,
        @NotEmpty @Size(min = 4)
        @Schema(
                description = "Closed polygon in GeoJSON [longitude, latitude] order. "
                        + "The backend calculates the plot area from this boundary."
        )
        List<@Valid @Size(min = 2, max = 2) List<@NotNull Double>> polygonCoordinates,
        @Size(max = 60) String cropType,
        @Size(max = 80) String variety,
        @Size(max = 120) String location,
        @Size(max = 60) String campaign,
        @Size(max = 500) String notes
) {
}
