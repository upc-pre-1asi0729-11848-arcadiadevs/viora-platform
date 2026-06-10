package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.IoTDeviceCommandService;
import com.arcadiadevs.viora.platform.agronomic.application.queryservices.IoTDeviceQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByPlotIdQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.CreateIoTDeviceResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.IoTDeviceResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.UpdateIoTDeviceResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.CreateIoTDeviceCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.IoTDeviceResourceFromIoTDeviceAssembler;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.UpdateIoTDeviceCommandFromResourceAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller that exposes IoT device endpoints.
 *
 * <p>
 * (TS12-005) GET    /api/v1/plots/{plotId}/iot-devices — list all devices belonging to a plot.<br>
 * (TS13-004) POST   /api/v1/plots/{plotId}/iot-devices — register a new IoT device for a plot.<br>
 * (TS014)    PATCH  /api/v1/plots/{plotId}/iot-devices/{deviceId} — update an existing IoT device.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/plots/{plotId}/iot-devices", produces = APPLICATION_JSON_VALUE)
@Tag(name = "IoT Devices", description = "IoT Device management endpoints")
public class IoTDevicesController {

    private final IoTDeviceCommandService ioTDeviceCommandService;
    private final IoTDeviceQueryService ioTDeviceQueryService;

    public IoTDevicesController(
            IoTDeviceCommandService ioTDeviceCommandService,
            IoTDeviceQueryService ioTDeviceQueryService) {
        this.ioTDeviceCommandService = ioTDeviceCommandService;
        this.ioTDeviceQueryService = ioTDeviceQueryService;
    }

    /**
     * Registers a new IoT device under the specified plot.
     * The requesting user must be the plot owner.
     *
     * @param plotId   the plot identifier (path variable)
     * @param resource the request body with userId, deviceName and optional status
     * @return 201 Created with IoTDeviceResource, or 403 Forbidden
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create IoT device",
            description = "Registers a new IoT device associated with a plot. Requires plot ownership.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "IoT device created successfully",
                    content = @Content(schema = @Schema(implementation = IoTDeviceResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plot")
    })
    public ResponseEntity<?> createIoTDevice(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,
            @Valid @RequestBody CreateIoTDeviceResource resource) {

        var command = CreateIoTDeviceCommandFromResourceAssembler.toCommandFromResource(resource, plotId);
        var result = ioTDeviceCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                IoTDeviceResourceFromIoTDeviceAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Returns all IoT devices registered under the specified plot.
     * The requesting user must be the plot owner.
     *
     * @param plotId the plot identifier (path variable)
     * @param userId the authenticated user identifier (query parameter)
     * @return 200 OK with list of IoTDeviceResource, or 403 Forbidden
     */
    @GetMapping
    @Operation(
            summary = "List IoT devices by plot",
            description = "Returns all IoT devices associated with a plot. Requires plot ownership.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Devices retrieved successfully",
                    content = @Content(schema = @Schema(implementation = IoTDeviceResource.class))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plot")
    })
    public ResponseEntity<?> getIoTDevicesByPlotId(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,
            @Parameter(description = "Authenticated user identifier", required = true)
            @RequestParam Long userId) {

        var query = new GetIoTDevicesByPlotIdQuery(plotId, userId);
        var result = ioTDeviceQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                devices -> devices.stream()
                        .map(IoTDeviceResourceFromIoTDeviceAssembler::toResourceFromEntity)
                        .toList(),
                HttpStatus.OK
        );
    }

    /**
     * Updates an existing IoT device's metadata under the specified plot.
     *
     * @param plotId   the plot identifier (path variable)
     * @param deviceId the device identifier (path variable)
     * @param resource the request body with deviceName and status
     * @return 200 OK with updated IoTDeviceResource, or 400/404
     */
    @PatchMapping(value = "/{deviceId}", consumes = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update IoT device",
            description = "Updates device name and status. Both fields are required.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device updated successfully",
                    content = @Content(schema = @Schema(implementation = IoTDeviceResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Plot or device not found")
    })
    public ResponseEntity<?> updateIoTDevice(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,
            @Parameter(description = "Device identifier", required = true)
            @PathVariable Long deviceId,
            @Valid @RequestBody UpdateIoTDeviceResource resource) {

        var command = UpdateIoTDeviceCommandFromResourceAssembler.toCommandFromResource(resource, plotId, deviceId);
        var result = ioTDeviceCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                IoTDeviceResourceFromIoTDeviceAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}
