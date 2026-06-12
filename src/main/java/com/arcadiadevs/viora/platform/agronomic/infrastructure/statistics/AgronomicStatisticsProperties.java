package com.arcadiadevs.viora.platform.agronomic.infrastructure.statistics;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for agronomic statistic snapshots and charts.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "agronomic.statistics")
public class AgronomicStatisticsProperties {

    /** Chill-portions accumulation target rendered as the chart threshold line. */
    @DecimalMin("0.0")
    private double chillPortionsThreshold = 600.0;

    /** Whether the periodic snapshot ingestion job is active. */
    private boolean scheduledIngestionEnabled = false;

    /** Cron expression for the periodic snapshot ingestion job. */
    private String ingestionCron = "0 0 2 * * *";
}
