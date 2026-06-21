package com.arcadiadevs.viora.platform.agronomic.interfaces.acl;

import java.util.List;
import java.util.Optional;

/**
 * ACL Facade that exposes Agronomic bounded context capabilities to other contexts.
 */
public interface AgronomicContextFacade {
    /**
     * Fetches the current NDVI value for a given plot.
     *
     * @param plotId Plot identifier
     * @param userId Owner user identifier
     * @return current NDVI value if available
     */
    Optional<Double> fetchCurrentNdviByPlotId(Long plotId, Long userId);

    /**
     * Resolves the display name of a plot by its identifier.
     *
     * @param plotId Plot identifier
     * @return the plot name if the plot exists
     */
    Optional<String> getPlotName(Long plotId);

    /**
     * Finds plots whose centroid lies within {@code radiusKm} of the reference
     * plot's centroid, excluding the reference plot itself. The result carries
     * only anonymized identifiers and distances (no owner or naming data), so it
     * is safe for community-risk diffusion across contexts.
     *
     * @param referencePlotId The plot at the center of the search radius.
     * @param radiusKm        The search radius in kilometers.
     * @return the neighbor plots within the radius, each with its distance.
     */
    List<NeighborPlot> findNeighborPlotsWithinRadius(Long referencePlotId, double radiusKm);
}
