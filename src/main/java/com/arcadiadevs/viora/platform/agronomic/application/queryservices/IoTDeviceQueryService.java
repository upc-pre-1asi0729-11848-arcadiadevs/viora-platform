package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByPlotIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application query service for IoTDevice.
 *
 * <p>
 * (TS12-004) Handles {@link GetIoTDevicesByPlotIdQuery}, validating plot ownership
 * before delegating to the repository.
 * </p>
 */
@Service
public class IoTDeviceQueryService {

    private final IoTDeviceRepository ioTDeviceRepository;
    private final PlotRepository plotRepository;

    public IoTDeviceQueryService(
            IoTDeviceRepository ioTDeviceRepository,
            PlotRepository plotRepository) {
        this.ioTDeviceRepository = ioTDeviceRepository;
        this.plotRepository = plotRepository;
    }

    /**
     * Returns all IoT devices for the given plot, provided the requesting user owns the plot.
     *
     * @param query the query containing plotId and authenticatedUserId
     * @return Success with a list of devices, or Failure if ownership check fails
     */
    @Transactional(readOnly = true)
    public Result<List<IoTDevice>, ApplicationError> handle(GetIoTDevicesByPlotIdQuery query) {
        var plot = plotRepository.findById(new PlotId(query.plotId()));

        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound(
                    "Plot",
                    String.valueOf(query.plotId())
            ));
        }

        if (!plot.get().belongsTo(new UserId(query.authenticatedUserId()))) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d".formatted(
                            query.authenticatedUserId(), query.plotId())));
        }

        List<IoTDevice> devices = ioTDeviceRepository.findAllByPlotId(query.plotId());
        return Result.success(devices);
    }
}
