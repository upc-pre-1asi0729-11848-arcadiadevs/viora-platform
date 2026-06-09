package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CreateIoTDeviceCommand;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Application service contract for commands over the IoTDevice aggregate.
 */
@Service
public interface IoTDeviceCommandService {

    /**
     * Handles IoT device creation.
     *
     * @param command command containing plot id, user id, device name and status
     * @return the created IoTDevice aggregate or an application error
     * @see CreateIoTDeviceCommand
     */
    Result<IoTDevice, ApplicationError> handle(CreateIoTDeviceCommand command);
}
