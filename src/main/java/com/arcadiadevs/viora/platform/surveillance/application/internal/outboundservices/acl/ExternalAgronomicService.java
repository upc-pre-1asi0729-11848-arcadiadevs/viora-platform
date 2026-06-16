package com.arcadiadevs.viora.platform.surveillance.application.internal.outboundservices.acl;

import com.arcadiadevs.viora.platform.agronomic.interfaces.acl.AgronomicContextFacade;
import org.springframework.stereotype.Service;

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
}
