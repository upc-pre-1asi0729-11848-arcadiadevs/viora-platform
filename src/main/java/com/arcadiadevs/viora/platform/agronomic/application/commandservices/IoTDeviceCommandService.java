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
public class IoTDeviceCommandService {

    public Result<IoTDevice, ApplicationError> handle(CreateIoTDeviceCommand command) {
        return null;
    }
}
