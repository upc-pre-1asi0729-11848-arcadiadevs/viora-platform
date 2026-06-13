package com.arcadiadevs.viora.platform.agronomic.infrastructure.statistics;

import com.arcadiadevs.viora.platform.agronomic.application.commandservices.AgronomicStatisticIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodic driver for agronomic statistic snapshot ingestion.
 *
 * <p>
 * Runs on the configured cron schedule but only acts when scheduled ingestion is
 * enabled, so the job is inert in environments (tests, local) where it should
 * not run automatically. On-demand ingestion remains available via the REST API.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AgronomicStatisticIngestionScheduler {

    private final AgronomicStatisticIngestionService ingestionService;
    private final AgronomicStatisticsProperties properties;

    @Scheduled(cron = "${agronomic.statistics.ingestion-cron:0 0 2 * * *}")
    public void ingestDailySnapshots() {
        if (!properties.isScheduledIngestionEnabled()) {
            return;
        }

        var report = ingestionService.ingestAllActivePlots();
        log.info(
                "Scheduled agronomic statistic ingestion completed: {} ingested, {} skipped.",
                report.ingested(),
                report.skipped()
        );
    }
}
