package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Externalized chill-requirement defaults, in Dynamic Model chill portions (CP).
 *
 * <p>Course-level defaults; validate with an agronomist before production use.</p>
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "agronomic.chill")
public class ChillProperties {

    /** Neutral requirement used when the plot's crop has no known default. */
    @Positive
    private double defaultRequirementPortions = 50.0;

    /** Per-crop chill requirement defaults keyed by crop name (case-insensitive). */
    private Map<String, Double> cropRequirementPortions = new LinkedHashMap<>();
}
