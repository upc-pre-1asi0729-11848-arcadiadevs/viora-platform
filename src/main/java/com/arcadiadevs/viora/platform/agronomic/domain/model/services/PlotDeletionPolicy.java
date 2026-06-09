package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;

/**
 * Plot deletion policy.
 *
 * <p>
 *     Domain service that defines whether a plot can be deleted
 *     and whether it should be physically removed or logically deactivated.
 * </p>
 */
public class PlotDeletionPolicy {

    /**
     * Checks whether the plot can be deleted.
     *
     * @param plot The plot to evaluate.
     * @return true if the plot can be deleted.
     */
    public boolean canDelete(Plot plot) {
        return plot != null && plot.isActive();
    }

    /**
     * Determines whether the plot should be logically deleted instead of physically removed.
     *
     * <p>
     *     In Viora, plots can be connected to monitoring summaries, IoT devices,
     *     alerts, nutrition plans or interventions. If related records exist,
     *     the plot should be deactivated to preserve traceability.
     * </p>
     *
     * @param hasRelatedOperationalRecords Indicates whether the plot has related records.
     * @return true if the plot should be logically deleted.
     */
    public boolean requiresLogicalDeletion(boolean hasRelatedOperationalRecords) {
        return hasRelatedOperationalRecords;
    }

    /**
     * Explains why a plot cannot be deleted.
     *
     * @param plot The plot evaluated.
     * @return The explanation message.
     */
    public String explainDeletionRejection(Plot plot) {
        if (plot == null) {
            return "Plot is required.";
        }

        if (!plot.isActive()) {
            return "Only active plots can be deleted.";
        }

        return "Plot cannot be deleted.";
    }
}