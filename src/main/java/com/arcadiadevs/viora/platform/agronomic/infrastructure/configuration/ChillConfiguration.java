package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the chill-requirement policy from externalized properties.
 */
@Configuration
@EnableConfigurationProperties(ChillProperties.class)
public class ChillConfiguration {

    @Bean
    ChillRequirementPolicy chillRequirementPolicy(ChillProperties properties) {
        return new ChillRequirementPolicy(
                properties.getDefaultRequirementPortions(),
                properties.getCropRequirementPortions()
        );
    }
}
