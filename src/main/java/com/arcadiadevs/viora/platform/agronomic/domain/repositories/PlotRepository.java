package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotName;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Plot repository port.
 *
 * <p>
 *     Defines the persistence operations required by the agronomic domain
 *     to manage plots without depending directly on JPA or database details.
 * </p>
 */
public interface PlotRepository {
    /**
     * Finds a plot by its ID.
     * @param id The plot ID.
     * @return The plot if found.
     */
    Optional<Plot> findById(PlotId id);

    /**
     * Finds all plots.
     * @return The list of plots.
     */
    List<Plot> findAll();

    /**
     * Finds all plots owned by a user.
     * @param userId The owner user ID.
     * @return The list of plots owned by the user.
     */
    List<Plot> findByUserId(UserId userId);

    /**
     * Finds a plot by name and owner user ID.
     * @param name The plot name.
     * @param userId The owner user ID.
     * @return The plot if found.
     */
    Optional<Plot> findByNameAndUserId(PlotName name, UserId userId);

    /**
     * Saves a plot.
     * @param plot The plot to save.
     * @return The saved plot.
     */
    Plot save(Plot plot);

    /**
     * Checks whether a plot exists by ID.
     * @param id The plot ID.
     * @return true if the plot exists, false otherwise.
     */
    boolean existsById(PlotId id);

    /**
     * Checks whether a plot name already exists for a specific user.
     * @param name The plot name.
     * @param userId The owner user ID.
     * @return true if the plot name exists for the user, false otherwise.
     */
    boolean existsByNameAndUserId(PlotName name, UserId userId);

    /**
     * Checks whether a plot name already exists for a specific user excluding a plot ID.
     * @param name The plot name.
     * @param userId The owner user ID.
     * @param id The plot ID to exclude.
     * @return true if another plot with the same name exists, false otherwise.
     */
    boolean existsByNameAndUserIdAndIdIsNot(PlotName name, UserId userId, PlotId id);

    /**
     * Checks whether the plot has related operational records.
     *
     * <p>
     * This can include monitoring summaries, IoT devices, alerts,
     * nutrition plans or interventions.
     * </p>
     *
     * @param id The plot ID.
     * @return true if the plot has related records, false otherwise.
     */
    boolean hasRelatedOperationalRecords(PlotId id);

    /**
     * Deletes a plot by ID.
     * @param id The plot ID.
     */
    void deleteById(PlotId id);
}