package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.readmodels.IoTDeviceReadout;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByPlotIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByUserIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.SoilReadingSimulator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application query service for IoTDevice reads.
 *
 * <p>
 * Devices are enriched with their current telemetry by the {@link SoilReadingSimulator}
 * (Viora has no physical sensors). Readings are derived from the owning plot's
 * geography and latest NDVI, so the dashboard stays coherent with the real plot.
 * </p>
 */
@Service
public class IoTDeviceQueryService {

    private final IoTDeviceRepository ioTDeviceRepository;
    private final PlotRepository plotRepository;
    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final SoilReadingSimulator soilReadingSimulator;

    public IoTDeviceQueryService(
            IoTDeviceRepository ioTDeviceRepository,
            PlotRepository plotRepository,
            AgronomicStatisticRepository agronomicStatisticRepository,
            SoilReadingSimulator soilReadingSimulator) {
        this.ioTDeviceRepository = ioTDeviceRepository;
        this.plotRepository = plotRepository;
        this.agronomicStatisticRepository = agronomicStatisticRepository;
        this.soilReadingSimulator = soilReadingSimulator;
    }

    /**
     * Returns all IoT devices for the given plot (with current telemetry), provided
     * the requesting user owns the plot.
     *
     * @param query the query containing plotId and authenticatedUserId
     * @return Success with the device readouts, or Failure if ownership check fails
     */
    @Transactional(readOnly = true)
    public Result<List<IoTDeviceReadout>, ApplicationError> handle(GetIoTDevicesByPlotIdQuery query) {
        var plot = plotRepository.findById(new PlotId(query.plotId()));

        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound("Plot", String.valueOf(query.plotId())));
        }

        if (!plot.get().belongsTo(new UserId(query.authenticatedUserId()))) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d".formatted(
                            query.authenticatedUserId(), query.plotId())));
        }

        Instant now = Instant.now();
        Double latestNdvi = latestNdviForPlot(plot.get());

        List<IoTDeviceReadout> readouts = ioTDeviceRepository.findAllByPlotId(query.plotId())
                .stream()
                .map(device -> toReadout(device, plot.get(), latestNdvi, now))
                .toList();

        return Result.success(readouts);
    }

    /**
     * Returns all of a user's IoT devices across their plots, each with current
     * telemetry. Backs the dashboard aggregate Water Stress view.
     *
     * @param query the query containing the owner userId
     * @return Success with the device readouts (possibly empty)
     */
    @Transactional(readOnly = true)
    public Result<List<IoTDeviceReadout>, ApplicationError> handle(GetIoTDevicesByUserIdQuery query) {
        List<Plot> plots = plotRepository.findByUserId(new UserId(query.userId()))
                .stream()
                .filter(Plot::isActive)
                .toList();

        if (plots.isEmpty()) {
            return Result.success(List.of());
        }

        Map<Long, Plot> plotsById = new HashMap<>();
        Map<Long, Double> ndviByPlotId = new HashMap<>();
        for (Plot plot : plots) {
            Long plotId = plot.getId().getValue();
            plotsById.put(plotId, plot);
            ndviByPlotId.put(plotId, latestNdviForPlot(plot));
        }

        Instant now = Instant.now();

        List<IoTDeviceReadout> readouts = ioTDeviceRepository.findAllByPlotIdIn(List.copyOf(plotsById.keySet()))
                .stream()
                .map(device -> {
                    Plot plot = plotsById.get(device.getPlotId());
                    return toReadout(device, plot, ndviByPlotId.get(device.getPlotId()), now);
                })
                .toList();

        return Result.success(readouts);
    }

    /** Builds a device readout, simulating its current telemetry from the plot. */
    private IoTDeviceReadout toReadout(IoTDevice device, Plot plot, Double latestNdvi, Instant now) {
        GeoPoint location = plot != null ? plot.getPolygonCoordinates().centroid() : null;
        IoTDeviceType type = device.getDeviceType() != null
                ? device.getDeviceType()
                : IoTDeviceType.WEATHER_STATION;

        SensorReadings readings = soilReadingSimulator.simulate(
                device.getActivationCode(), type, location, latestNdvi, now);

        return new IoTDeviceReadout(device, readings);
    }

    /** Most recent NDVI for a plot, or null when the plot has no statistics yet. */
    private Double latestNdviForPlot(Plot plot) {
        return agronomicStatisticRepository.findLatestByPlotId(plot.getId())
                .map(statistic -> statistic.getNdviValue().getValue())
                .orElse(null);
    }
}
