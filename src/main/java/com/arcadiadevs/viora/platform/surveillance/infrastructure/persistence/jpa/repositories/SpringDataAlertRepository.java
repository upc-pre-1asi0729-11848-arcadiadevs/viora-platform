package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertStatus;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Alert entities.
 */
@Repository
public interface SpringDataAlertRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> findByPlotIdInOrderByCreatedAtDesc(List<Long> plotIds, Pageable pageable);

    /** Recent alerts for the given plots, excluding a status (used to hide dismissed alerts). */
    List<AlertEntity> findByPlotIdInAndStatusNotOrderByCreatedAtDesc(
            List<Long> plotIds, AlertStatus status, Pageable pageable);

    List<AlertEntity> findByPlotIdInAndStatus(List<Long> plotIds, AlertStatus status);

    /** Most recent alert originated by the given pest sighting report, if any. */
    Optional<AlertEntity> findFirstByReportIdOrderByIdDesc(Long reportId);
}
