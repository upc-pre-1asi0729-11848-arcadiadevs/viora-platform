package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Alert entities.
 */
@Repository
public interface SpringDataAlertRepository extends JpaRepository<AlertEntity, Long> {
}
