package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldEstimationPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the yield-estimation policy from externalized properties.
 */
@Configuration
@EnableConfigurationProperties(YieldForecastProperties.class)
public class YieldForecastConfiguration {

    @Bean
    YieldEstimationPolicy yieldEstimationPolicy(YieldForecastProperties properties) {
        return new YieldEstimationPolicy(
                properties.getBaseYieldTonnesPerHectare(),
                properties.getNdviFloor(),
                properties.getNdviOptimal(),
                properties.getChillMinFactor()
        );
    }
}
