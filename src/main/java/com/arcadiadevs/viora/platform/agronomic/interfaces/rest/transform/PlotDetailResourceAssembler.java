package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.PlotDetail;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.PlotDetailResource;

import java.util.List;

/**
 * Maps the Plot Detail application projection to its REST resource.
 */
public final class PlotDetailResourceAssembler {

    private PlotDetailResourceAssembler() {
    }

    public static PlotDetailResource toResourceFromReadModel(PlotDetail detail) {
        var plot = detail.plot();
        return new PlotDetailResource(
                plot.getId().getValue(),
                plot.getUserId().getValue(),
                plot.getName().getValue(),
                plot.getLocation(),
                plot.getCampaign(),
                plot.getCropType(),
                plot.getVariety(),
                plot.getNotes(),
                toCoordinateResource(plot.getPolygonCoordinates().getPoints()),
                plot.getAreaSize().getHectares(),
                boundaryPointCount(plot.getPolygonCoordinates().getPoints()),
                detail.boundaryStatus(),
                detail.registeredAt(),
                detail.lastConfigurationUpdateAt(),
                new PlotDetailResource.MonitoringLinksResource(
                        detail.climateMonitoring().name(),
                        detail.satelliteNdvi().name(),
                        detail.climateLastSyncAt(),
                        detail.satelliteLastSyncAt()
                ),
                new PlotDetailResource.IoTSummaryResource(
                        detail.iotTelemetry().name(),
                        detail.devices().size(),
                        detail.onlineDeviceCount(),
                        detail.lastIotActivityAt()
                ),
                detail.devices().stream()
                        .map(device -> new PlotDetailResource.IoTDeviceDetailResource(
                                device.device().getId(),
                                device.device().getDeviceName(),
                                device.device().getStatus().name(),
                                device.linkedAt(),
                                device.lastActivityAt()
                        ))
                        .toList(),
                detail.recentConfigurationActivity().stream()
                        .map(activity -> new PlotDetailResource.ConfigurationActivityResource(
                                activity.type(),
                                activity.description(),
                                activity.occurredAt()
                        ))
                        .toList()
        );
    }

    private static List<List<Double>> toCoordinateResource(List<GeoPoint> points) {
        return points.stream()
                .map(point -> List.of(point.getLongitude(), point.getLatitude()))
                .toList();
    }

    private static int boundaryPointCount(List<GeoPoint> points) {
        if (points.size() > 1 && points.getFirst().equals(points.getLast())) {
            return points.size() - 1;
        }
        return points.size();
    }
}
