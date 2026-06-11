package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.MonitoringSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for MonitoringSummary entities.
 */
public interface SpringDataMonitoringSummaryRepository extends JpaRepository<MonitoringSummaryEntity, Long> {

    /**
     * Finds all monitoring summary entities for a specific user ID.
     * @param userId The ID of the user.
     * @return A list of monitoring summary entities for the user.
     */
    List<MonitoringSummaryEntity> findAllByUserId(Long userId);

    /**
     * Finds a monitoring summary entity for a specific user ID and measurement date.
     * @param userId The ID of the user.
     * @param measurementDate The measurement date.
     * @return The monitoring summary entity if found.
     */
    Optional<MonitoringSummaryEntity> findByUserIdAndMeasurementDate(Long userId, LocalDate measurementDate);
}