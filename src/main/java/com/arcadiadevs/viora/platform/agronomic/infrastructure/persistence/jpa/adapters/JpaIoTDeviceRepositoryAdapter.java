package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.IoTDeviceEntityFromIoTDeviceAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.IoTDeviceFromIoTDeviceEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataIoTDeviceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter that bridges the domain IoTDeviceRepository contract
 * and the Spring Data persistence layer.
 * (TS12-002)
 */
@Repository
public class JpaIoTDeviceRepositoryAdapter implements IoTDeviceRepository {

    private final SpringDataIoTDeviceRepository springDataIoTDeviceRepository;

    public JpaIoTDeviceRepositoryAdapter(SpringDataIoTDeviceRepository springDataIoTDeviceRepository) {
        this.springDataIoTDeviceRepository = springDataIoTDeviceRepository;
    }

    @Override
    public IoTDevice save(IoTDevice device) {
        var entity = IoTDeviceEntityFromIoTDeviceAssembler.toEntityFromAggregate(device);
        var saved = springDataIoTDeviceRepository.save(entity);
        return IoTDeviceFromIoTDeviceEntityAssembler.toAggregateFromEntity(saved);
    }

    @Override
    public Optional<IoTDevice> findById(Long id) {
        return springDataIoTDeviceRepository.findById(id)
                .map(IoTDeviceFromIoTDeviceEntityAssembler::toAggregateFromEntity);
    }

    @Override
    public List<IoTDevice> findAllByPlotId(Long plotId) {
        return springDataIoTDeviceRepository.findAllByPlotId(plotId)
                .stream()
                .map(IoTDeviceFromIoTDeviceEntityAssembler::toAggregateFromEntity)
                .toList();
    }

    @Override
    public Optional<IoTDevice> findByIdAndPlotId(Long id, Long plotId) {
        return springDataIoTDeviceRepository.findByIdAndPlotId(id, plotId)
                .map(IoTDeviceFromIoTDeviceEntityAssembler::toAggregateFromEntity);
    }

    @Override
    public boolean existsByIdAndPlotId(Long id, Long plotId) {
        return springDataIoTDeviceRepository.existsByIdAndPlotId(id, plotId);
    }

    @Override
    public void delete(IoTDevice device) {
        if (device.getId() != null) {
            springDataIoTDeviceRepository.deleteById(device.getId());
        }
    }
}
