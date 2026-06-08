package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for the IoTDevice aggregate.
 * (TS12-001)
 */
public interface IoTDeviceRepository {

    IoTDevice save(IoTDevice device);

    Optional<IoTDevice> findById(Long id);

    List<IoTDevice> findAllByPlotId(Long plotId);

    Optional<IoTDevice> findByIdAndPlotId(Long id, Long plotId);

    boolean existsByIdAndPlotId(Long id, Long plotId);

    void delete(IoTDevice device);
}
