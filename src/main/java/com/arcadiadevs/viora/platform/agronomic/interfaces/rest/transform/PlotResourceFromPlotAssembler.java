package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotResource;

import java.util.List;
import java.util.Objects;

/**
 * Assembler to convert Plot aggregate into PlotResource.
 */
public final class PlotResourceFromPlotAssembler {

    private PlotResourceFromPlotAssembler() {
    }

    /**
     * Converts a Plot aggregate into a PlotResource.
     *
     * @param plot The Plot aggregate.
     * @return The PlotResource.
     */
    public static PlotResource toResourceFromAggregate(Plot plot) {
        Objects.requireNonNull(plot, "Plot aggregate is required.");

        return new PlotResource(
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
                mapActivityState(plot.getActive())
        );
    }

    /**
     * Converts the plot polygon points into a frontend-friendly coordinate list.
     *
     * @param points The polygon points.
     * @return The coordinate list.
     */
    private static List<List<Double>> toCoordinateResource(List<GeoPoint> points) {
        return points.stream()
                .map(point -> List.of(point.getLongitude(), point.getLatitude()))
                .toList();
    }

    private static String mapActivityState(Boolean active) {
        return Boolean.TRUE.equals(active) ? "enable" : "disable";
    }
}

