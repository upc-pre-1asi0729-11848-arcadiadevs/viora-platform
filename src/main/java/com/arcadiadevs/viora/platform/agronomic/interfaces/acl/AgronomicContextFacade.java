package com.arcadiadevs.viora.platform.agronomic.interfaces.acl;

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
}
