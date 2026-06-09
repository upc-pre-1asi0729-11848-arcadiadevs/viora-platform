package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.PlotFromPlotPersistenceEntityAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.assemblers.PlotPersistenceEntityFromPlotAssembler;
import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories.SpringDataPlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the PlotRepository domain port.
 *
 * <p>
 *     This adapter connects the agronomic domain with Spring Data JPA persistence.
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class JpaPlotRepositoryAdapter implements PlotRepository {

    /**
     * Spring Data JPA repository.
     */
    private final SpringDataPlotRepository springDataPlotRepository;

    /**
     * Finds a plot by ID.
     *
     * @param id The plot ID.
     * @return The plot if found.
     */
    @Override
    public Optional<Plot> findById(Long id) {
        return springDataPlotRepository.findById(id)
                .map(PlotFromPlotPersistenceEntityAssembler::toAggregateFromEntity);
    }

    /**
     * Finds all plots.
     *
     * @return The list of plots.
     */
    @Override
    public List<Plot> findAll() {
        return springDataPlotRepository.findAll()
                .stream()
                .map(PlotFromPlotPersistenceEntityAssembler::toAggregateFromEntity)
                .toList();
    }

    /**
     * Finds all active plots owned by a user.
     *
     * @param userId The owner user ID.
     * @return The list of active plots.
     */
    @Override
    public List<Plot> findByUserId(Long userId) {
        return springDataPlotRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(PlotFromPlotPersistenceEntityAssembler::toAggregateFromEntity)
                .toList();
    }

    /**
     * Finds a plot by name and owner user ID.
     *
     * @param name The plot name.
     * @param userId The owner user ID.
     * @return The plot if found.
     */
    @Override
    public Optional<Plot> findByNameAndUserId(String name, Long userId) {
        return springDataPlotRepository.findByNameAndUserId(name, userId)
                .map(PlotFromPlotPersistenceEntityAssembler::toAggregateFromEntity);
    }

    /**
     * Saves a plot.
     *
     * @param plot The plot to save.
     * @return The saved plot.
     */
    @Override
    public Plot save(Plot plot) {
        var entity = PlotPersistenceEntityFromPlotAssembler.toEntityFromAggregate(plot);
        var savedEntity = springDataPlotRepository.save(entity);
        return PlotFromPlotPersistenceEntityAssembler.toAggregateFromEntity(savedEntity);
    }

    /**
     * Checks whether a plot exists by ID.
     *
     * @param id The plot ID.
     * @return true if the plot exists.
     */
    @Override
    public boolean existsById(Long id) {
        return springDataPlotRepository.existsById(id);
    }

    /**
     * Checks whether a plot name exists for a user.
     *
     * @param name The plot name.
     * @param userId The owner user ID.
     * @return true if the plot name exists.
     */
    @Override
    public boolean existsByNameAndUserId(String name, Long userId) {
        return springDataPlotRepository.existsByNameAndUserId(name, userId);
    }

    /**
     * Checks whether another plot with the same name exists for the user.
     *
     * @param name The plot name.
     * @param userId The owner user ID.
     * @param id The plot ID to exclude.
     * @return true if another plot with the same name exists.
     */
    @Override
    public boolean existsByNameAndUserIdAndIdIsNot(String name, Long userId, Long id) {
        return springDataPlotRepository.existsByNameAndUserIdAndIdIsNot(name, userId, id);
    }

    /**
     * Checks whether the plot has related operational records.
     *
     * <p>
     * Temporary implementation for Sprint 3. When monitoring summaries,
     * IoT devices, nutrition plans, alerts or interventions are implemented,
     * this method should validate those dependencies.
     * </p>
     *
     * @param id The plot ID.
     * @return false for now.
     */
    @Override
    public boolean hasRelatedOperationalRecords(Long id) {
        return false;
    }

    /**
     * Deletes a plot by ID.
     *
     * @param id The plot ID.
     */
    @Override
    public void deleteById(Long id) {
        springDataPlotRepository.deleteById(id);
    }
}