package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Enables typed configuration for the AgroMonitoring client.
 *
 * <p>
 * Applies connect and read timeouts so a slow or unresponsive provider cannot
 * stall platform requests indefinitely.
 * </p>
 */
@Configuration
@EnableConfigurationProperties(AgroMonitoringProperties.class)
public class AgroMonitoringClientConfiguration {

    @Bean
    RestClient agroMonitoringRestClient(AgroMonitoringProperties properties) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(properties.getConnectTimeoutMillis()));
        requestFactory.setReadTimeout(Duration.ofMillis(properties.getReadTimeoutMillis()));

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
