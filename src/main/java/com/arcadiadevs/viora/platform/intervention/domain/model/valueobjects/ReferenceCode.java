package com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.util.UUID;

/**
 * Value object representing a unique reference code for an intervention.
 */
@Embeddable
public record ReferenceCode(String code) {

    public ReferenceCode() {
        this(UUID.randomUUID().toString());
    }

    public static ReferenceCode generate() {
        return new ReferenceCode("REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
