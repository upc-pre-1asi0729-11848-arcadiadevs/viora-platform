package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources;

import java.util.List;

/**
 * Community-risk snapshot around a reference plot: anonymized nearby signals
 * detected within a monitoring radius, plus derived preventive recommendations.
 *
 * @param plotId                    The reference plot identifier.
 * @param plotName                  The reference plot display name.
 * @param radiusKm                  The monitoring radius in kilometers.
 * @param signals                   The anonymized nearby risk signals.
 * @param preventiveRecommendations Derived preventive recommendations.
 */
public record CommunityRiskResource(
        Long plotId,
        String plotName,
        double radiusKm,
        List<NearbyRiskSignalResource> signals,
        List<String> preventiveRecommendations
) {
}
