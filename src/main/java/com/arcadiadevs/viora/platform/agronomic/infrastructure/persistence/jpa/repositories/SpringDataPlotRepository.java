package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities.PlotPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for PlotPersistenceEntity.
 *
 * <p>
 * Provides database operations for plot persistence.
 * </p>
 */
@Repository
public interface SpringDataPlotRepository extends JpaRepository<PlotPersistenceEntity, Long> {

    /**
     * Finds all active plots owned by a user.
     *
     * @param userId The owner user ID.
     * @return The list of active plots.
     */
    List<PlotPersistenceEntity> findByUserIdAndActiveTrue(Long userId);

    /**
     * Finds a plot by name and owner user ID.
     *
     * @param name The plot name.
     * @param userId The owner user ID.
     * @return The plot entity if found.
     */
    Optional<PlotPersistenceEntity> findByNameAndUserId(String name, Long userId);

    /**
     * Checks whether a plot name already exists for a specific user.
     *
     * @param name The plot name.
     * @param userId The owner user ID.
     * @return true if the plot name exists.
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * Checks whether another plot with the same name exists for the user.
     *
     * @param name The plot name.
     * @param userId The owner user ID.
     * @param id The plot ID to exclude.
     * @return true if another plot with the same name exists.
     */
    boolean existsByNameAndUserIdAndIdIsNot(String name, Long userId, Long id);
}
