package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Enables typed configuration for the AgroMonitoring client.
 */
@Configuration
@EnableConfigurationProperties(AgroMonitoringProperties.class)
public class AgroMonitoringClientConfiguration {

    @Bean
    RestClient agroMonitoringRestClient(AgroMonitoringProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
