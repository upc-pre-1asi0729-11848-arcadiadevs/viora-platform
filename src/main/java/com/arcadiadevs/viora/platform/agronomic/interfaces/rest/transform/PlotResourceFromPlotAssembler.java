package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotResource;

import java.util.List;

/**
 * Assembler to convert Plot aggregate into PlotResource.
 */
public class PlotResourceFromPlotAssembler {

    /**
     * Converts a Plot aggregate into a PlotResource.
     *
     * @param plot The Plot aggregate.
     * @return The PlotResource.
     */
    public static PlotResource toResourceFromAggregate(Plot plot) {
        if (plot == null) return null;

        return new PlotResource(
                plot.getId(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                toCoordinateResource(plot.getPolygonCoordinates().getPoints()),
                plot.getAreaSize().getHectares(),
                plot.getCropType(),
                plot.getVariety(),
                plot.getActive()
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
                .map(point -> List.of(point.getLatitude(), point.getLongitude()))
                .toList();
    }
}