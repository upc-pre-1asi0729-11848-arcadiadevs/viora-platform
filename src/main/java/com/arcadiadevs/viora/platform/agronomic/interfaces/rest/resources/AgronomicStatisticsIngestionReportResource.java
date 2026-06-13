package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

/**
 * REST projection of an agronomic statistic ingestion run outcome.
 *
 * @param ingested Number of new daily snapshots persisted.
 * @param skipped Number of plots skipped (already snapshotted today or no real signal).
 */
public record AgronomicStatisticsIngestionReportResource(int ingested, int skipped) {
}
