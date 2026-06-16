package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Spring Data JPA repository for Alert entities.
 */
@Repository
public interface SpringDataAlertRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> findByPlotIdInOrderByCreatedAtDesc(List<Long> plotIds, Pageable pageable);
}
