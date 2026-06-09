package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.IoTDeviceQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByPlotIdQuery;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources.IoTDeviceResource;
import com.arcadiadevs.viora.platform.agronomic.interfaces.rest.transform.IoTDeviceResourceFromIoTDeviceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for IoT device operations.
 * <p>
 * (TS12-005) Exposes GET /api/v1/plots/{plotId}/iot-devices
 */
@RestController
@RequestMapping("/api/v1/plots/{plotId}/iot-devices")
@Tag(name = "IoT Devices", description = "IoT Device management endpoints")
public class IoTDevicesController {

    private final IoTDeviceQueryService ioTDeviceQueryService;

    public IoTDevicesController(IoTDeviceQueryService ioTDeviceQueryService) {
        this.ioTDeviceQueryService = ioTDeviceQueryService;
    }

    /**
     * GET /api/v1/plots/{plotId}/iot-devices?userId={userId}
     * <p>
     * Returns all IoT devices registered under the specified plot.
     * The requesting user must be the plot owner.
     *
     * @param plotId the plot identifier (path variable)
     * @param userId the authenticated user identifier (query parameter)
     * @return 200 OK with list of IoTDeviceResource, or 403 Forbidden
     */
    @GetMapping
    @Operation(summary = "List IoT devices by plot",
            description = "Returns all IoT devices associated with a plot. Requires plot ownership.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devices retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not own the plot")
    })
    public ResponseEntity<List<IoTDeviceResource>> getIoTDevicesByPlotId(
            @Parameter(description = "Plot identifier", required = true)
            @PathVariable Long plotId,
            @Parameter(description = "Authenticated user identifier", required = true)
            @RequestParam Long userId) {

        var query = new GetIoTDevicesByPlotIdQuery(plotId, userId);

        var result = ioTDeviceQueryService.handle(query);

        if (result.isFailure()) {
            return ResponseEntity.status(403).build();
        }

        List<IoTDeviceResource> resources = result.toOptional()
                .orElseThrow()
                .stream()
                .map(IoTDeviceResourceFromIoTDeviceAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}
