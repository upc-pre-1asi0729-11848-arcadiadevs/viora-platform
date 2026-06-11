package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Externalized configuration for dynamic nutrition rules.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "agronomic.dynamic-nutrition")
public class DynamicNutritionProperties {

    private double temperatureReferenceCelsius = 20.0;

    @DecimalMin("-1.0")
    @DecimalMax("1.0")
    private double highRiskNdviThreshold = 0.30;

    @DecimalMin("-1.0")
    @DecimalMax("1.0")
    private double moderateRiskNdviThreshold = 0.50;

    @Min(1)
    private int highRiskWindowDays = 3;

    @Min(1)
    private int extremeRiskWindowDays = 2;

    @Positive
    private double foliarSupportDosageLitersPerHectare = 2.5;

    @Positive
    private double potassiumCalciumDosageKilogramsPerHectare = 3.0;

    @Positive
    private double biostimulantDosageLitersPerHectare = 1.2;
}
