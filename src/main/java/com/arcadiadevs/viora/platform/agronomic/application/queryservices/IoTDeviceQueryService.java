package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByPlotIdQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application query service for IoTDevice.
 * <p>
 * (TS12-004) Handles {@link GetIoTDevicesByPlotIdQuery}, validating plot ownership
 * before delegating to the repository.
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
     * @return Success with a list of devices, or Failure with FORBIDDEN if ownership check fails
     */
    public Result<List<IoTDevice>, ApplicationError> handle(GetIoTDevicesByPlotIdQuery query) {
        boolean ownsPlot = plotRepository.existsByIdAndOwnerUserId(
                query.plotId(), query.authenticatedUserId());

        if (!ownsPlot) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "plot-ownership",
                    "User %s does not own plot %s".formatted(
                            query.authenticatedUserId(), query.plotId())));
        }

        List<IoTDevice> devices = ioTDeviceRepository.findAllByPlotId(query.plotId());
        return Result.success(devices);
    }
}
