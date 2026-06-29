package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.DeleteIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.UpdateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ActivationCodeCatalog;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;
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
    private final ActivationCodeCatalog activationCodeCatalog;

    public IoTDeviceCommandService(
            IoTDeviceRepository ioTDeviceRepository,
            PlotRepository plotRepository,
            ActivationCodeCatalog activationCodeCatalog) {
        this.ioTDeviceRepository = ioTDeviceRepository;
        this.plotRepository = plotRepository;
        this.activationCodeCatalog = activationCodeCatalog;
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

        // Parse the claim code: a malformed code is a client input error (400).
        ActivationCode activationCode;
        try {
            activationCode = new ActivationCode(command.activationCode());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("activationCode", e.getMessage()));
        }

        // The code must correspond to a real issued unit (422 otherwise).
        if (!activationCodeCatalog.isIssued(activationCode)) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "activation-code",
                    "Activation code %s is not recognized.".formatted(activationCode.value())));
        }

        // A physical unit can only be claimed once (409 otherwise).
        if (ioTDeviceRepository.existsByActivationCode(activationCode.value())) {
            return Result.failure(ApplicationError.conflict(
                    "activation-code",
                    "Activation code %s has already been claimed.".formatted(activationCode.value())));
        }

        var device = IoTDevice.claim(
                plotId,
                new DeviceName(command.deviceName()),
                command.status(),
                activationCode
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
