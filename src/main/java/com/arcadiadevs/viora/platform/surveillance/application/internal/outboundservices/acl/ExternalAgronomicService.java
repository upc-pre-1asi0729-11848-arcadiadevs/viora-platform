package com.arcadiadevs.viora.platform.surveillance.application.internal.outboundservices.acl;

import com.arcadiadevs.viora.platform.agronomic.interfaces.acl.AgronomicContextFacade;
import com.arcadiadevs.viora.platform.agronomic.interfaces.acl.NeighborPlot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ACL service used by the Surveillance bounded context to interact with Agronomic capabilities.
 */
@Service
public class ExternalAgronomicService {

    private final AgronomicContextFacade agronomicContextFacade;

    public ExternalAgronomicService(AgronomicContextFacade agronomicContextFacade) {
        this.agronomicContextFacade = agronomicContextFacade;
    }

    /**
     * Fetches the current NDVI value for a given plot and user.
     *
     * @param plotId the plot identifier
     * @param userId the user identifier
     * @return optional NDVI value
     */
    public Optional<Double> fetchCurrentNdviByPlotId(Long plotId, Long userId) {
        return agronomicContextFacade.fetchCurrentNdviByPlotId(plotId, userId);
    }

    /**
     * Resolves the display name of a plot.
     *
     * @param plotId the plot identifier
     * @return the plot name if it exists
     */
    public Optional<String> getPlotName(Long plotId) {
        return agronomicContextFacade.getPlotName(plotId);
    }

    /**
     * Finds anonymized neighbor plots within a radius of the reference plot.
     *
     * @param referencePlotId the plot at the center of the search radius
     * @param radiusKm        the search radius in kilometers
     * @return the neighbor plots (id + distance) within the radius
     */
    public List<NeighborPlot> findNeighborPlotsWithinRadius(Long referencePlotId, double radiusKm) {
        return agronomicContextFacade.findNeighborPlotsWithinRadius(referencePlotId, radiusKm);
    }
}
