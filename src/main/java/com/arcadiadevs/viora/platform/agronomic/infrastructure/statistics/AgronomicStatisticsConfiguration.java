package com.arcadiadevs.viora.platform.agronomic.infrastructure.statistics;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables agronomic statistic configuration properties and scheduling support
 * for periodic snapshot ingestion.
 */
@Configuration
@EnableConfigurationProperties(AgronomicStatisticsProperties.class)
@EnableScheduling
public class AgronomicStatisticsConfiguration {
}
