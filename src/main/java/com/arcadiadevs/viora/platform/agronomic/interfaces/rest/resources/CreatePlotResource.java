package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request resource used to register a productive agricultural plot.
 */
public record CreatePlotResource(
        @NotNull @Positive Long userId,
        @NotBlank @Size(min = 3, max = 80) String name,
        @NotEmpty @Size(min = 4)
        @Schema(
                description = "Closed polygon in GeoJSON [longitude, latitude] order."
        )
        List<@Valid @Size(min = 2, max = 2) List<@NotNull Double>> polygonCoordinates,
        @NotNull @Positive @DecimalMax("99999999.99") @Digits(integer = 8, fraction = 2)
        BigDecimal areaSizeHectares,
        @Size(max = 60) String cropType,
        @Size(max = 80) String variety,
        @Size(max = 120) String location,
        @Size(max = 60) String campaign,
        @Size(max = 500) String notes
) {
}
