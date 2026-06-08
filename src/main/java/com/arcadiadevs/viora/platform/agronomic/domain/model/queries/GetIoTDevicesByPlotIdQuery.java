package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query to retrieve all IoT devices associated with a specific plot.
 * (TS12-003) The authenticated user must be the owner of the plot.
 *
 * @param plotId              the plot whose devices are requested
 * @param authenticatedUserId the user making the request (used for ownership validation)
 */
public record GetIoTDevicesByPlotIdQuery(Long plotId, Long authenticatedUserId) {
    public GetIoTDevicesByPlotIdQuery {
        if (plotId == null)
            throw new IllegalArgumentException("GetIoTDevicesByPlotIdQuery requires a valid plotId");
        if (authenticatedUserId == null)
            throw new IllegalArgumentException("GetIoTDevicesByPlotIdQuery requires a valid authenticatedUserId");
    }
}
