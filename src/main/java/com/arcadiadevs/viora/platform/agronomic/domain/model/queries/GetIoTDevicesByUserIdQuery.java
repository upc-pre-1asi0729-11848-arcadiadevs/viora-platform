package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query to retrieve all IoT devices owned by a user across their plots, used by
 * the dashboard's aggregate telemetry view.
 *
 * @param userId the owner whose devices are requested
 */
public record GetIoTDevicesByUserIdQuery(Long userId) {
    public GetIoTDevicesByUserIdQuery {
        if (userId == null)
            throw new IllegalArgumentException("GetIoTDevicesByUserIdQuery requires a valid userId");
    }
}
