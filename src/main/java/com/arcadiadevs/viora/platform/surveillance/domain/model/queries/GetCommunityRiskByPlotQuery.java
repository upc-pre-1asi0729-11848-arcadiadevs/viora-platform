package com.arcadiadevs.viora.platform.surveillance.domain.model.queries;

/**
 * Query for the anonymized community-risk snapshot around a reference plot.
 *
 * @param plotId   The reference plot identifier.
 * @param radiusKm The monitoring radius in kilometers.
 */
public record GetCommunityRiskByPlotQuery(Long plotId, double radiusKm) {

    public GetCommunityRiskByPlotQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number.");
        }
        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be a positive number.");
        }
    }
}
