package com.arcadiadevs.viora.platform.agronomic.interfaces.acl;

/**
 * Anonymized neighbor-plot reference exposed across bounded contexts.
 *
 * <p>Carries only the plot identifier and its great-circle distance (in
 * kilometers) from a reference plot, intentionally excluding any owner or
 * naming information so consumers (e.g. community-risk diffusion) cannot
 * de-anonymize the neighbor.</p>
 *
 * @param plotId     The neighbor plot identifier.
 * @param distanceKm Distance from the reference plot centroid, in kilometers.
 */
public record NeighborPlot(Long plotId, double distanceKm) {
}
