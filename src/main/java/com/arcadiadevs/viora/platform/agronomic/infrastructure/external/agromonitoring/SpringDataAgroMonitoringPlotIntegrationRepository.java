package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for AgroMonitoring plot correlations and cached imagery.
 */
public interface SpringDataAgroMonitoringPlotIntegrationRepository
        extends JpaRepository<AgroMonitoringPlotIntegrationEntity, Long> {

    Optional<AgroMonitoringPlotIntegrationEntity> findByPlotId(Long plotId);
}
