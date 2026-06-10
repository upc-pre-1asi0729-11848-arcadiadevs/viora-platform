package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.Instant;

/**
 * Satellite imagery exposed to the web application.
 *
 * @param id Provider imagery identifier.
 * @param plotId Associated Viora plot identifier.
 * @param tileUrl Raster tile URL template.
 * @param captureDate Imagery capture instant.
 * @param ndviMean Mean NDVI value.
 * @param cloudPercentage Cloud coverage percentage.
 */
public record SatelliteImageryResource(
        String id,
        Long plotId,
        String tileUrl,
        Instant captureDate,
        Double ndviMean,
        Double cloudPercentage
) {
}
