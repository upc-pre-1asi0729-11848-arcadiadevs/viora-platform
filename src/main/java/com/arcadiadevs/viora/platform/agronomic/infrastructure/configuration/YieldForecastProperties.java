package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Externalized parameters for the yield-estimation heuristic.
 *
 * <p>Course-level defaults; validate with an agronomist before production use.</p>
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "agronomic.yield-forecast")
public class YieldForecastProperties {

    @Positive
    private double baseYieldTonnesPerHectare = 4.0;

    @DecimalMin("-1.0")
    @DecimalMax("1.0")
    private double ndviFloor = 0.20;

    @DecimalMin("-1.0")
    @DecimalMax("1.0")
    private double ndviOptimal = 0.80;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double chillMinFactor = 0.60;
}
