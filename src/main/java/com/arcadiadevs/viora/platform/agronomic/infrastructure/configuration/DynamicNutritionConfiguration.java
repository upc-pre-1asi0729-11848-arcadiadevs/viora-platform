package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configures dynamic nutrition policy and deterministic application time.
 */
@Configuration
@EnableConfigurationProperties(DynamicNutritionProperties.class)
public class DynamicNutritionConfiguration {

    @Bean
    DynamicNutritionPolicy dynamicNutritionPolicy(DynamicNutritionProperties properties) {
        return new DynamicNutritionPolicy(
                properties.getTemperatureReferenceCelsius(),
                properties.getHighRiskNdviThreshold(),
                properties.getModerateRiskNdviThreshold(),
                properties.getHighRiskWindowDays(),
                properties.getExtremeRiskWindowDays(),
                properties.getFoliarSupportDosageLitersPerHectare(),
                properties.getPotassiumCalciumDosageKilogramsPerHectare(),
                properties.getBiostimulantDosageLitersPerHectare()
        );
    }

    @Bean
    Clock applicationClock() {
        return Clock.systemUTC();
    }
}
