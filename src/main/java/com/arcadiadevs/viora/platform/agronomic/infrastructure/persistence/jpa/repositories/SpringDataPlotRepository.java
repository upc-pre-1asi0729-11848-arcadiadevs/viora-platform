package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.PlotEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA interface for PlotEntity.
 * Provides plot ownership check needed by IoTDevice services.
 */
@Repository
public interface SpringDataPlotRepository extends JpaRepository<PlotEntity, Long> {

    boolean existsByIdAndOwnerUserId(Long id, Long ownerUserId);
}
