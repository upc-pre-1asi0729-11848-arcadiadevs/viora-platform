package com.arcadiadevs.viora.platform.agronomic.application.readmodels;

/**
 * Outcome of an agronomic statistic snapshot ingestion run.
 *
 * @param ingested Number of new daily snapshots persisted.
 * @param skipped Number of plots skipped (already snapshotted today or no real signal).
 */
public record AgronomicStatisticsIngestionReport(int ingested, int skipped) {

    public static AgronomicStatisticsIngestionReport empty() {
        return new AgronomicStatisticsIngestionReport(0, 0);
    }

    public AgronomicStatisticsIngestionReport withIngested() {
        return new AgronomicStatisticsIngestionReport(ingested + 1, skipped);
    }

    public AgronomicStatisticsIngestionReport withSkipped() {
        return new AgronomicStatisticsIngestionReport(ingested, skipped + 1);
    }
}
