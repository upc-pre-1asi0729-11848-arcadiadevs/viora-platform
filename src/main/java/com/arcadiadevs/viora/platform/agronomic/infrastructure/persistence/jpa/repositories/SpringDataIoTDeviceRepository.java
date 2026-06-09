package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.IoTDeviceEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA interface for IoTDeviceEntity.
 * (TS12-002)
 */
@Repository
public interface SpringDataIoTDeviceRepository extends JpaRepository<IoTDeviceEntity, Long> {

    List<IoTDeviceEntity> findAllByPlotId(Long plotId);

    Optional<IoTDeviceEntity> findByIdAndPlotId(Long id, Long plotId);

    boolean existsByIdAndPlotId(Long id, Long plotId);
}
