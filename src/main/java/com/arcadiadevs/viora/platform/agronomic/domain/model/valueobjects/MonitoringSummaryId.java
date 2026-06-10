package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Monitoring summary identifier value object.
 */
@Getter
@EqualsAndHashCode
public class MonitoringSummaryId {

    private final Long value;

    public MonitoringSummaryId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Monitoring summary ID must be a positive number.");
        }
        this.value = value;
    }
}