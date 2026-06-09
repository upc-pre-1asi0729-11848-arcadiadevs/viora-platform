package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.IoTDeviceQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByPlotIdQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.IoTDeviceResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.IoTDeviceResourceFromIoTDeviceAssembler;
import com.arcadiadevs.viora.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller that exposes IoT device endpoints.
 *
 * <p>
 * (TS12-005) GET /api/v1/plots/{plotId}/iot-devices — list all devices belonging to a plot.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/plots/{plotId}/iot-devices", produces = APPLICATION_JSON_VALUE)
@Tag(name = "IoT Devices", description = "IoT Device management endpoints")
public class IoTDevicesController {

    private final IoTDeviceQueryService ioTDeviceQueryService;

    public IoTDevicesController(IoTDeviceQueryService ioTDeviceQueryService) {
        this.ioTDeviceQueryService = ioTDeviceQueryService;
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
}