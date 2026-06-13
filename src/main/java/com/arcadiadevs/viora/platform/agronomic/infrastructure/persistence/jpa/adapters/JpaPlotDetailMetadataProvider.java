package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotDetailMetadataProvider;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring.SpringDataAgroMonitoringPlotIntegrationRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataIoTDeviceRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataPlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA-backed provider for the audit timestamps used by Plot Detail.
 */
@Component
@RequiredArgsConstructor
public class JpaPlotDetailMetadataProvider implements PlotDetailMetadataProvider {

    private final SpringDataPlotRepository plotRepository;
    private final SpringDataIoTDeviceRepository ioTDeviceRepository;
    private final SpringDataAgroMonitoringPlotIntegrationRepository integrationRepository;

    @Override
    public Optional<PlotMetadata> findByPlotId(PlotId plotId) {
        return plotRepository.findById(plotId.getValue())
                .map(plot -> new PlotMetadata(
                        plot.getCreatedAt(),
                        plot.getUpdatedAt(),
                        integrationRepository.findByPlotId(plotId.getValue())
                                .map(integration -> new MonitoringIntegrationMetadata(
                                        integration.getCreatedAt(),
                                        integration.getLastCheckedAt(),
                                        integration.getCaptureDate()
                                ))
                                .orElse(null),
                        ioTDeviceRepository.findAllByPlotId(plotId.getValue())
                                .stream()
                                .map(device -> new DeviceMetadata(
                                        device.getId(),
                                        device.getCreatedAt(),
                                        device.getUpdatedAt()
                                ))
                                .toList()
                ));
    }
}
