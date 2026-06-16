package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PestSightingReportId;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.PestSightingReportRepository;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.PestSightingReportEntityFromPestSightingReportAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.PestSightingReportFromPestSightingReportEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataPestSightingReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPestSightingReportRepositoryAdapter implements PestSightingReportRepository {

    private final SpringDataPestSightingReportRepository springDataRepository;

    @Override
    public PestSightingReport save(PestSightingReport report) {
        var entity = PestSightingReportEntityFromPestSightingReportAssembler.toEntityFromAggregate(report);
        var savedEntity = springDataRepository.save(entity);
        return PestSightingReportFromPestSightingReportEntityAssembler.toAggregateFromEntity(savedEntity);
    }

    @Override
    public Optional<PestSightingReport> findById(PestSightingReportId id) {
        return springDataRepository.findById(id.value())
                .map(PestSightingReportFromPestSightingReportEntityAssembler::toAggregateFromEntity);
    }
}
