package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotRegistration;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotRegistrationResource;

import java.util.List;

/**
 * Maps the plot registration read model to its REST representation.
 */
public final class PlotRegistrationResourceAssembler {

    private PlotRegistrationResourceAssembler() {
    }

    public static PlotRegistrationResource toResourceFromReadModel(PlotRegistration registration) {
        var plot = registration.plot();
        return new PlotRegistrationResource(
                plot.getId().getValue(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                toCoordinateResource(plot.getPolygonCoordinates().getPoints()),
                plot.getAreaSize().getHectares(),
                plot.getCropType(),
                plot.getVariety(),
                plot.getLocation(),
                plot.getCampaign(),
                plot.getNotes(),
                plot.isActive() ? "enable" : "disable",
                registration.climateMonitoring().name(),
                registration.satelliteNdvi().name(),
                registration.iotDevices().name()
        );
    }

    private static List<List<Double>> toCoordinateResource(List<GeoPoint> points) {
        return points.stream()
                .map(point -> List.of(point.getLongitude(), point.getLatitude()))
                .toList();
    }
}
