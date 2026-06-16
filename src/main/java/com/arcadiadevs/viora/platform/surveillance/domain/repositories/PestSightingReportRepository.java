package com.arcadiadevs.viora.platform.surveillance.domain.repositories;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PestSightingReportId;

import java.util.Optional;

/**
 * Repository for Pest Sighting Reports.
 */
public interface PestSightingReportRepository {
    PestSightingReport save(PestSightingReport report);
    Optional<PestSightingReport> findById(PestSightingReportId id);
}
