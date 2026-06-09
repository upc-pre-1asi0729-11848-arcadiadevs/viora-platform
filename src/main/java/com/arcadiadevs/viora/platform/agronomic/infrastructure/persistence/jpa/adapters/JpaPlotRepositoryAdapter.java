package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataPlotRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA adapter that bridges the domain PlotRepository contract
 * and the Spring Data persistence layer.
 */
@Repository
public class JpaPlotRepositoryAdapter implements PlotRepository {

    private final SpringDataPlotRepository springRepo;

    public JpaPlotRepositoryAdapter(SpringDataPlotRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public boolean existsByIdAndOwnerUserId(Long plotId, Long userId) {
        return springRepo.existsByIdAndOwnerUserId(plotId, userId);
    }
}
