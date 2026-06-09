package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data repository for agronomic statistic aggregate root.
 *
 * <p>
 * This repository is responsible for querying agronomic statistics
 * from the persistence layer using Spring Data JPA.
 * </p>
 */
@Repository
public interface SpringDataAgronomicStatisticRepository extends JpaRepository<AgronomicStatistic, Long> {

    /**
     * Finds all agronomic statistics by user id and measurement date range.
     *
     * @param userId The user identifier.
     * @param startDate The start measurement date.
     * @param endDate The end measurement date.
     * @return A list of agronomic statistics.
     */
    @Query("""
            SELECT statistic
            FROM AgronomicStatistic statistic
            WHERE statistic.userId.userId = :userId
            AND statistic.measurementDate.measurementDate BETWEEN :startDate AND :endDate
            ORDER BY statistic.measurementDate.measurementDate ASC
            """)
    List<AgronomicStatistic> findAllByUserIdAndMeasurementDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Finds all agronomic statistics by user id, plot id and measurement date range.
     *
     * @param userId The user identifier.
     * @param plotId The plot identifier.
     * @param startDate The start measurement date.
     * @param endDate The end measurement date.
     * @return A list of agronomic statistics.
     */
    @Query("""
            SELECT statistic
            FROM AgronomicStatistic statistic
            WHERE statistic.userId.userId = :userId
            AND statistic.plotId.plotId = :plotId
            AND statistic.measurementDate.measurementDate BETWEEN :startDate AND :endDate
            ORDER BY statistic.measurementDate.measurementDate ASC
            """)
    List<AgronomicStatistic> findAllByUserIdAndPlotIdAndMeasurementDateBetween(
            @Param("userId") Long userId,
            @Param("plotId") Long plotId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}