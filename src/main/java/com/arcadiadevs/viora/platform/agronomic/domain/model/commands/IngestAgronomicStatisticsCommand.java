package com.arcadiadevs.viora.platform.agronomic.domain.model.commands;

/**
 * Command to ingest today's agronomic statistic snapshots for a user's plots.
 *
 * @param userId Owner whose active plots are snapshotted.
 */
public record IngestAgronomicStatisticsCommand(Long userId) {

    public IngestAgronomicStatisticsCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }
}
