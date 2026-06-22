package com.arcadiadevs.viora.platform.surveillance.domain.repositories;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import java.util.Optional;

/**
 * Repository interface for Alert aggregates.
 */
public interface AlertRepository {
    Alert save(Alert alert);
    Optional<Alert> findById(Long id);

    /** Finds the most recent alert raised from the given pest sighting report, if any. */
    Optional<Alert> findByReportId(Long reportId);
    // Other finders as necessary
}
