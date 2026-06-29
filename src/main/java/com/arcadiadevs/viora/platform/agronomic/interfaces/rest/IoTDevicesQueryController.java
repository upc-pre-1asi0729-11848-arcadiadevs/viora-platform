package com.arcadiadevs.viora.platform.agronomic.interfaces.rest;

import com.arcadiadevs.viora.platform.agronomic.application.queryservices.IoTDeviceQueryService;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetIoTDevicesByUserIdQuery;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing the aggregate IoT devices view for a producer.
 *
 * <p>
 * GET /api/v1/iot-devices?userId — lists every IoT device the user owns across
 * all of their plots, each enriched with its current (simulated) telemetry. Backs
 * the dashboard Water Stress cards, which aggregate readings across plots.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/iot-devices", produces = APPLICATION_JSON_VALUE)
@Tag(name = "IoT Devices", description = "IoT Device management endpoints")
public class IoTDevicesQueryController {

    private final IoTDeviceQueryService ioTDeviceQueryService;

    public IoTDevicesQueryController(IoTDeviceQueryService ioTDeviceQueryService) {
        this.ioTDeviceQueryService = ioTDeviceQueryService;
    }

    /**
     * Lists all of the user's IoT devices across plots, with current telemetry.
     *
     * @param userId the authenticated user identifier (query parameter)
     * @return 200 OK with a list of IoTDeviceResource (possibly empty)
     */
    @GetMapping
    @Operation(
            summary = "List the user's IoT devices",
            description = "Returns all IoT devices owned by the user across their plots, with current telemetry.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Devices retrieved successfully",
                    content = @Content(schema = @Schema(implementation = IoTDeviceResource.class)))
    })
    public ResponseEntity<?> getMyIoTDevices(
            @Parameter(description = "Authenticated user identifier", required = true)
            @RequestParam Long userId) {

        var result = ioTDeviceQueryService.handle(new GetIoTDevicesByUserIdQuery(userId));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                readouts -> readouts.stream()
                        .map(IoTDeviceResourceFromIoTDeviceAssembler::toResourceFromReadout)
                        .toList(),
                HttpStatus.OK
        );
    }
}
