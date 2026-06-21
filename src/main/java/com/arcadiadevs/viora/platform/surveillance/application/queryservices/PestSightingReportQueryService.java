package com.arcadiadevs.viora.platform.surveillance.application.queryservices;

import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetPestSightingReportsByUserQuery;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.PestSightingReportFromPestSightingReportEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataPestSightingReportRepository;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.PestSightingReportResource;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform.PestSightingReportResourceFromPestSightingReportAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Query service for reading pest sighting reports (the symptom report history).
 */
@Service
@RequiredArgsConstructor
public class PestSightingReportQueryService {

    private final SpringDataPestSightingReportRepository repository;

    /**
     * Handles {@link GetPestSightingReportsByUserQuery}.
     *
     * @param query the query carrying the reporter user identifier
     * @return the user's reports, newest first
     */
    public List<PestSightingReportResource> handle(GetPestSightingReportsByUserQuery query) {
        return repository.findByReporterUserIdOrderByIdDesc(query.reporterUserId()).stream()
                .map(PestSightingReportFromPestSightingReportEntityAssembler::toAggregateFromEntity)
                .map(PestSightingReportResourceFromPestSightingReportAssembler::toResourceFromAggregate)
                .toList();
    }
}
