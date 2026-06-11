package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MonitoringSummaryId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;

import java.util.List;
import java.util.Optional;

/**
 * Monitoring summary repository port.
 *
 * <p>
 * Defines the persistence operations required by the agronomic domain
 * to retrieve and record monitoring summaries.
 * </p>
 */
public interface MonitoringSummaryRepository {

    /**
     * Finds a monitoring summary by its ID.
     * @param id The monitoring summary ID.
     * @return The monitoring summary if found.
     */
    Optional<MonitoringSummary> findById(MonitoringSummaryId id);

    /**
     * Finds all monitoring summaries for a specific user.
     * @param userId The ID of the user.
     * @return A list of monitoring summaries for the user.
     */
    List<MonitoringSummary> findAllByUserId(UserId userId);

    /**
     * Finds a monitoring summary for a specific user and measurement date.
     * @param userId The ID of the user.
     * @param measurementDate The measurement date.
     * @return The monitoring summary if found.
     */
    Optional<MonitoringSummary> findByUserIdAndMeasurementDate(UserId userId, MeasurementDate measurementDate);

    /**
     * Saves a monitoring summary.
     * @param monitoringSummary The monitoring summary to save.
     * @return The saved monitoring summary.
     */
    MonitoringSummary save(MonitoringSummary monitoringSummary);
}