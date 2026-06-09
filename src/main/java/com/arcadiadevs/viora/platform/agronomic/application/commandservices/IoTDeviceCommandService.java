package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.commands.UpdateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.application.ports.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

/**
 * Application service that orchestrates IoT device update workflow.
 */
@NullMarked
@Service
public class IoTDeviceCommandService {

    private final IoTDeviceRepository repository;
    private final PlotOwnershipValidator ownershipValidator;

    public IoTDeviceCommandService(IoTDeviceRepository repository, PlotOwnershipValidator ownershipValidator) {
        this.repository = repository;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Handles an UpdateIoTDeviceCommand: validates ownership, finds the device,
     * applies changes, persists, and returns the updated device.
     *
     * @param command the update command
     * @return Result with the updated device or an application error
     */
    public Result<IoTDevice, ApplicationError> handle(UpdateIoTDeviceCommand command) {
        var ownershipResult = ownershipValidator.validate(command.plotId(), command.authenticatedUserId());
        if (ownershipResult.isFailure()) {
            return Result.failure(ownershipResult.failure().orElseThrow().error());
        }

        var device = repository.findByIdAndPlotId(command.deviceId(), command.plotId());
        if (device.isEmpty()) {
            return Result.failure(ApplicationError.notFound("IoT device", String.valueOf(command.deviceId())));
        }

        IoTDevice iotDevice = device.get();
        iotDevice.update(command.deviceName(), command.iotDeviceStatus());
        IoTDevice saved = repository.save(iotDevice);

        return Result.success(saved);
    }
}
