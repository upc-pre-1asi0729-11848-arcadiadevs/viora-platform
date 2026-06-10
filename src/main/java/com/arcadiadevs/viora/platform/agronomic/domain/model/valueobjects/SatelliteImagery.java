package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.time.Instant;

/**
 * Latest satellite imagery available for a plot.
 *
 * @param id Provider imagery identifier.
 * @param tileUrl Tile URL template compatible with web map clients.
 * @param captureDate Imagery capture instant.
 * @param ndviMean Mean NDVI value for the plot.
 * @param cloudPercentage Cloud coverage percentage.
 */
public record SatelliteImagery(
        String id,
        String tileUrl,
        Instant captureDate,
        Double ndviMean,
        Double cloudPercentage
) {
    public SatelliteImagery {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Satellite imagery ID is required.");
        }
        if (tileUrl == null || tileUrl.isBlank()) {
            throw new IllegalArgumentException("Satellite imagery tile URL is required.");
        }
        if (captureDate == null) {
            throw new IllegalArgumentException("Satellite imagery capture date is required.");
        }
        if (ndviMean != null && (!Double.isFinite(ndviMean) || ndviMean < -1 || ndviMean > 1)) {
            throw new IllegalArgumentException("NDVI mean must be between -1 and 1.");
        }
        if (cloudPercentage == null
                || !Double.isFinite(cloudPercentage)
                || cloudPercentage < 0
                || cloudPercentage > 100) {
            throw new IllegalArgumentException("Cloud percentage must be between 0 and 100.");
        }
    }
}
