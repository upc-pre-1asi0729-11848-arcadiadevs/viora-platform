package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotWithCurrentImagery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotWithCurrentImageryResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.SatelliteImageryResource;

import java.util.List;
import java.util.Objects;

/**
 * Maps the plot imagery read model to its REST representation.
 */
public final class PlotWithCurrentImageryResourceAssembler {

    /**
     * Tile URL template served to web map clients. Tiles are streamed through the
     * platform proxy so the imagery provider API key never reaches the client.
     */
    private static final String TILE_PROXY_URL_TEMPLATE =
            "/api/v1/plots/%d/imagery/tile/{z}/{x}/{y}?userId=%d";

    private PlotWithCurrentImageryResourceAssembler() {
    }

    public static PlotWithCurrentImageryResource toResourceFromReadModel(
            PlotWithCurrentImagery readModel
    ) {
        Objects.requireNonNull(readModel, "Plot imagery read model is required.");

        var plot = readModel.plot();
        var imagery = readModel.currentImagery()
                .map(value -> new SatelliteImageryResource(
                        value.id(),
                        plot.getId().getValue(),
                        TILE_PROXY_URL_TEMPLATE.formatted(
                                plot.getId().getValue(),
                                plot.getUserId().getValue()
                        ),
                        value.captureDate(),
                        value.ndviMean(),
                        value.cloudPercentage()
                ))
                .orElse(null);

        return new PlotWithCurrentImageryResource(
                plot.getId().getValue(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                toCoordinateResource(plot.getPolygonCoordinates().getPoints()),
                plot.getAreaSize().getHectares(),
                imagery != null ? imagery.captureDate() : null,
                plot.getCropType(),
                plot.getVariety(),
                plot.isActive() ? "enable" : "disable",
                null,
                null,
                imagery
        );
    }

    private static List<List<Double>> toCoordinateResource(List<GeoPoint> points) {
        return points.stream()
                .map(point -> List.of(point.getLongitude(), point.getLatitude()))
                .toList();
    }
}
