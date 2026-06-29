package com.arcadiadevs.viora.platform.agronomic.infrastructure.configuration;

import com.arcadiadevs.viora.platform.agronomic.domain.model.services.ActivationCodeCatalog;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * In-memory implementation of {@link ActivationCodeCatalog} seeded with the
 * activation codes issued for the Viora demo fleet. Each code maps (by its prefix)
 * to a sensor kind: SP = soil probe, LW = leaf wetness, WS = weather station.
 *
 * <p>
 * Kept as a small fixed whitelist while there is no manufacturer integration; swap
 * for a persisted/remote registry without touching the domain.
 * </p>
 */
@Component
public class InMemoryActivationCodeCatalog implements ActivationCodeCatalog {

    private static final Set<String> ISSUED_CODES = Set.of(
            // Soil probes (soil moisture + soil temperature)
            "VIORA-SP01-7K3M",
            "VIORA-SP02-9XQ2",
            "VIORA-SP03-4D8R",
            // Leaf wetness sensors (leaf humidity)
            "VIORA-LW01-2H6T",
            "VIORA-LW02-5N1P",
            "VIORA-LW03-8B4V",
            // Weather stations (all metrics)
            "VIORA-WS01-3F9C",
            "VIORA-WS02-6J2L",
            "VIORA-WS03-1Z7Y"
    );

    @Override
    public boolean isIssued(ActivationCode code) {
        return code != null && ISSUED_CODES.contains(code.value());
    }
}
