package com.arcadiadevs.viora.platform.surveillance.domain.model.queries;

/**
 * Query for the pest sighting reports submitted by a given user.
 *
 * @param reporterUserId The reporter user identifier.
 */
public record GetPestSightingReportsByUserQuery(Long reporterUserId) {

    public GetPestSightingReportsByUserQuery {
        if (reporterUserId == null || reporterUserId <= 0) {
            throw new IllegalArgumentException("Reporter user ID must be a positive number.");
        }
    }
}
