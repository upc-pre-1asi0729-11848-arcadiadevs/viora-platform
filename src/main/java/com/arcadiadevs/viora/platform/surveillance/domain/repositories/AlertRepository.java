package com.arcadiadevs.viora.platform.surveillance.domain.repositories;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import java.util.Optional;

/**
 * Repository interface for Alert aggregates.
 */
public interface AlertRepository {
    Alert save(Alert alert);
    Optional<Alert> findById(Long id);
    // Other finders as necessary
}
