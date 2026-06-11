package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.MyPlotsOverview;
import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotMonitoringOverview;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MyPlotOverviewResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.MyPlotsOverviewResource;

import java.util.List;

/**
 * Maps the My Plots application projection to its REST response.
 */
public final class MyPlotsOverviewResourceAssembler {

    private MyPlotsOverviewResourceAssembler() {
    }

    public static MyPlotsOverviewResource toResourceFromReadModel(MyPlotsOverview overview) {
        return new MyPlotsOverviewResource(
                overview.registeredPlotCount(),
                overview.monitoredAreaHectares(),
                overview.climateLinkedPlotCount(),
                overview.onlineDeviceCount(),
                overview.plots().stream()
                        .map(MyPlotsOverviewResourceAssembler::toPlotResource)
                        .toList()
        );
    }

    private static MyPlotOverviewResource toPlotResource(PlotMonitoringOverview overview) {
        var plot = overview.plot();
        return new MyPlotOverviewResource(
                plot.getId().getValue(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                plot.getLocation(),
                plot.getCampaign(),
                plot.getCropType(),
                plot.getVariety(),
                toCoordinateResource(plot.getPolygonCoordinates().getPoints()),
                plot.getAreaSize().getHectares(),
                overview.currentNdvi(),
                overview.chillPortions(),
                overview.healthStatus().name(),
                overview.onlineDeviceCount(),
                overview.activeAlertCount(),
                overview.lastUpdatedAt(),
                overview.climateMonitoring().name(),
                overview.satelliteNdvi().name()
        );
    }

    private static List<List<Double>> toCoordinateResource(List<GeoPoint> points) {
        return points.stream()
                .map(point -> List.of(point.getLongitude(), point.getLatitude()))
                .toList();
    }
}
