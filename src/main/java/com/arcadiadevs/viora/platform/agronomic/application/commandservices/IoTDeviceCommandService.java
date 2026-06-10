package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Application service for commands over the IoTDevice aggregate.
 * (TS13-004) Handles CreateIoTDeviceCommand.
 */
@Service
public class IoTDeviceCommandService {

    private final IoTDeviceRepository ioTDeviceRepository;
    private final PlotRepository plotRepository;

    public IoTDeviceCommandService(
            IoTDeviceRepository ioTDeviceRepository,
            PlotRepository plotRepository) {
        this.ioTDeviceRepository = ioTDeviceRepository;
        this.plotRepository = plotRepository;
    }

    /**
     * Registers a new IoT device under the specified plot.
     * The requesting user must own the plot.
     *
     * @param command the creation command with plotId, userId, deviceName and status
     * @return Success with the created IoTDevice, or Failure if ownership check fails
     */
    public Result<IoTDevice, ApplicationError> handle(CreateIoTDeviceCommand command) {
        var plotId = new PlotId(command.plotId());
        var userId = new UserId(command.authenticatedUserId());

        var plot = plotRepository.findById(plotId);
        if (plot.isEmpty() || !plot.get().belongsTo(userId)) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "plot-ownership",
                    "User %d does not own plot %d".formatted(
                            command.authenticatedUserId(), command.plotId())));
        }

        var device = new IoTDevice(
                plotId,
                new DeviceName(command.deviceName()),
                command.status()
        );

        var saved = ioTDeviceRepository.save(device);
        return Result.success(saved);
    }
}
