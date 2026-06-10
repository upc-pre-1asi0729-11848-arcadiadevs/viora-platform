package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeleteIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for commands over the IoTDevice aggregate.
 * (TS13-004) Handles CreateIoTDeviceCommand.
 * (TS014) Handles UpdateIoTDeviceCommand and DeleteIoTDeviceCommand.
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
     *
     * @param command the creation command with plotId, deviceName and status
     * @return Success with the created IoTDevice, or Failure if plot not found
     */
    @Transactional
    public Result<IoTDevice, ApplicationError> handle(CreateIoTDeviceCommand command) {
        var plotId = new PlotId(command.plotId());

        var plot = plotRepository.findById(plotId);
        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound(
                    "Plot",
                    String.valueOf(command.plotId())));
        }

        var device = new IoTDevice(
                plotId,
                new DeviceName(command.deviceName()),
                command.status()
        );

        var saved = ioTDeviceRepository.save(device);
        return Result.success(saved);
    }

    /**
     * Updates an existing IoT device's metadata under the specified plot.
     *
     * @param command the update command with plotId, deviceId, deviceName and status
     * @return Success with the updated IoTDevice, or Failure if plot or device not found
     */
    @Transactional
    public Result<IoTDevice, ApplicationError> handle(UpdateIoTDeviceCommand command) {
        var plotId = new PlotId(command.plotId());

        var plot = plotRepository.findById(plotId);
        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound(
                    "Plot",
                    String.valueOf(command.plotId())));
        }

        var device = ioTDeviceRepository.findByIdAndPlotId(command.deviceId(), command.plotId());
        if (device.isEmpty()) {
            return Result.failure(ApplicationError.notFound("IoT_device", String.valueOf(command.deviceId())));
        }

        IoTDevice iotDevice = device.get();
        iotDevice.update(new DeviceName(command.deviceName()), command.status());
        IoTDevice saved = ioTDeviceRepository.save(iotDevice);

        return Result.success(saved);
    }

    /**
     * Deletes an existing IoT device from the specified plot.
     *
     * @param command the delete command with plotId and deviceId
     * @return Success with Void if deleted, or Failure if plot or device not found
     */
    @Transactional
    public Result<Boolean, ApplicationError> handle(DeleteIoTDeviceCommand command) {
        var plotId = new PlotId(command.plotId());

        var plot = plotRepository.findById(plotId);
        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound(
                    "Plot",
                    String.valueOf(command.plotId())));
        }

        var device = ioTDeviceRepository.findByIdAndPlotId(command.deviceId(), command.plotId());
        if (device.isEmpty()) {
            return Result.failure(ApplicationError.notFound("IoT_device", String.valueOf(command.deviceId())));
        }

        ioTDeviceRepository.delete(device.get());

        return Result.success(true);
    }
}
