package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.MonitoringSummary;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MeasurementDate;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.MonitoringSummaryId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.MonitoringSummaryRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.MonitoringSummaryEntityFromMonitoringSummaryAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.MonitoringSummaryFromMonitoringSummaryEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.MonitoringSummaryEntity;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataMonitoringSummaryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter for the MonitoringSummaryRepository domain port, implementing persistence operations
 * using Spring Data JPA.
 */
@Repository
public class JpaMonitoringSummaryRepositoryAdapter implements MonitoringSummaryRepository {

    private final SpringDataMonitoringSummaryRepository springDataMonitoringSummaryRepository;
    private final MonitoringSummaryEntityFromMonitoringSummaryAssembler toEntityAssembler;
    private final MonitoringSummaryFromMonitoringSummaryEntityAssembler toDomainAssembler;

    public JpaMonitoringSummaryRepositoryAdapter(
            SpringDataMonitoringSummaryRepository springDataMonitoringSummaryRepository,
            MonitoringSummaryEntityFromMonitoringSummaryAssembler toEntityAssembler,
            MonitoringSummaryFromMonitoringSummaryEntityAssembler toDomainAssembler
    ) {
        this.springDataMonitoringSummaryRepository = springDataMonitoringSummaryRepository;
        this.toEntityAssembler = toEntityAssembler;
        this.toDomainAssembler = toDomainAssembler;
    }

    @Override
    public Optional<MonitoringSummary> findById(MonitoringSummaryId id) {
        return springDataMonitoringSummaryRepository.findById(id.getValue())
                .map(toDomainAssembler::toDomain);
    }

    @Override
    public List<MonitoringSummary> findAllByUserId(UserId userId) {
        return springDataMonitoringSummaryRepository.findAllByUserId(userId.getValue()).stream()
                .map(toDomainAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MonitoringSummary> findByUserIdAndMeasurementDate(UserId userId, MeasurementDate measurementDate) {
        return springDataMonitoringSummaryRepository.findByUserIdAndMeasurementDate(userId.getValue(), measurementDate.getValue())
                .map(toDomainAssembler::toDomain);
    }

    @Override
    public MonitoringSummary save(MonitoringSummary monitoringSummary) {
        MonitoringSummaryEntity entity = toEntityAssembler.toEntity(monitoringSummary);
        MonitoringSummaryEntity savedEntity = springDataMonitoringSummaryRepository.save(entity);
        return toDomainAssembler.toDomain(savedEntity);
    }
}