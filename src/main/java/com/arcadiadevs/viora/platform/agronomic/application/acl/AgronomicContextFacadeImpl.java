package com.arcadiadevs.viora.platform.agronomic.application.acl;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.PlotMonitoringSummaryQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetPlotMonitoringSummaryQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.agronomic.interfaces.acl.AgronomicContextFacade;
import com.arcadiadevs.viora.platform.agronomic.interfaces.acl.NeighborPlot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application-layer implementation of the Agronomic ACL facade.
 *
 * <p>Provides a simplified integration surface for other bounded contexts that need agronomic
 * operations without coupling to Agronomic internal models.</p>
 */
@Service
public class AgronomicContextFacadeImpl implements AgronomicContextFacade {

    private final PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService;
    private final PlotRepository plotRepository;

    public AgronomicContextFacadeImpl(
            PlotMonitoringSummaryQueryService plotMonitoringSummaryQueryService,
            PlotRepository plotRepository) {
        this.plotMonitoringSummaryQueryService = plotMonitoringSummaryQueryService;
        this.plotRepository = plotRepository;
    }

    /**
     * Fetches the current NDVI value for a given plot through the Agronomic query service.
     *
     * @param plotId Plot identifier
     * @param userId Owner user identifier
     * @return current NDVI value if available
     */
    @Override
    public Optional<Double> fetchCurrentNdviByPlotId(Long plotId, Long userId) {
        var query = new GetPlotMonitoringSummaryQuery(userId, plotId);
        var result = plotMonitoringSummaryQueryService.handle(query);
        return result.toOptional().map(summary -> summary.currentNdvi());
    }

    @Override
    public Optional<String> getPlotName(Long plotId) {
        return plotRepository.findById(new PlotId(plotId))
                .map(plot -> plot.getName().getValue());
    }

    @Override
    public List<NeighborPlot> findNeighborPlotsWithinRadius(Long referencePlotId, double radiusKm) {
        var reference = plotRepository.findById(new PlotId(referencePlotId)).orElse(null);

        if (reference == null) {
            return List.of();
        }

        GeoPoint referenceCentroid = reference.getPolygonCoordinates().centroid();

        return plotRepository.findAll().stream()
                .filter(plot -> !plot.getId().getValue().equals(referencePlotId))
                .map(plot -> toNeighbor(plot, referenceCentroid))
                .filter(neighbor -> neighbor.distanceKm() <= radiusKm)
                .toList();
    }

    private NeighborPlot toNeighbor(Plot plot, GeoPoint referenceCentroid) {
        double distanceKm = referenceCentroid.haversineKilometers(plot.getPolygonCoordinates().centroid());
        double roundedKm = Math.round(distanceKm * 10.0) / 10.0;
        return new NeighborPlot(plot.getId().getValue(), roundedKm);
    }
}
