package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.IoTDevice;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DeviceName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.IoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.IoTDeviceEntity;
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

    private final SpringDataIoTDeviceRepository springRepo;

    public JpaIoTDeviceRepositoryAdapter(SpringDataIoTDeviceRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public IoTDevice save(IoTDevice device) {
        IoTDeviceEntity entity = toEntity(device);
        IoTDeviceEntity saved = springRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<IoTDevice> findById(Long id) {
        return springRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<IoTDevice> findAllByPlotId(Long plotId) {
        return springRepo.findAllByPlotId(plotId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<IoTDevice> findByIdAndPlotId(Long id, Long plotId) {
        return springRepo.findByIdAndPlotId(id, plotId)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByIdAndPlotId(Long id, Long plotId) {
        return springRepo.existsByIdAndPlotId(id, plotId);
    }

    @Override
    public void delete(IoTDevice device) {
        if (device.getId() != null) {
            springRepo.deleteById(device.getId());
        }
    }

    // --- Mapping helpers ---

    private IoTDeviceEntity toEntity(IoTDevice domain) {
        IoTDeviceEntity entity = new IoTDeviceEntity();

        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        if (domain.getPlotId() != null) {
            entity.setPlotId(domain.getPlotId());
        }
        if (domain.getDeviceName() != null) {
            entity.setDeviceName(domain.getDeviceName());
        }

        entity.setStatus(domain.getStatus());
        return entity;
    }

    private IoTDevice toDomain(IoTDeviceEntity entity) {
        IoTDevice device = new IoTDevice(
                new PlotId(entity.getPlotId()),
                new DeviceName(entity.getDeviceName()),
                entity.getStatus()
        );

        if (entity.getId() != null) {
            device.setId(entity.getId());
        }

        return device;
    }
}