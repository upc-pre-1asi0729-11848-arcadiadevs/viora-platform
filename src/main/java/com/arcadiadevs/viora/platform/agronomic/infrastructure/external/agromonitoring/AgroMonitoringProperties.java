package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the AgroMonitoring integration.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "integrations.agromonitoring")
public class AgroMonitoringProperties {
    private boolean enabled;
    private String baseUrl = "https://api.agromonitoring.com";
    private String apiKey = "";
    @Min(1)
    private int imageryLookbackDays = 30;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double maximumCloudPercentage = 100.0;
    @Min(0)
    private int refreshIntervalMinutes = 60;
    @Min(0)
    private int weatherRefreshMinutes = 60;
    @Min(1)
    private int connectTimeoutMillis = 3000;
    @Min(1)
    private int readTimeoutMillis = 8000;
    @Min(0)
    private int quotaCooldownMinutes = 15;
    @Min(0)
    private int forecastCacheTtlMinutes = 360;
    @Min(0)
    private int currentWeatherCacheTtlMinutes = 60;
    @Min(0)
    private int weatherHistoryCacheTtlMinutes = 720;
    @Min(0)
    private int ndviHistoryCacheTtlMinutes = 720;

    public boolean isConfigured() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }
}
