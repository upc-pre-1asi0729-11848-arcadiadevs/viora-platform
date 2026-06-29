package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeneralHealthStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SensorHealthEvaluatorTest {

    private final SensorHealthEvaluator evaluator = new SensorHealthEvaluator();
    private final Instant now = Instant.parse("2026-06-29T12:00:00Z");

    @Test
    void unknownWhenThereAreNoReadings() {
        assertEquals(GeneralHealthStatus.UNKNOWN, evaluator.evaluate(null));
        assertEquals(GeneralHealthStatus.UNKNOWN,
                evaluator.evaluate(new SensorReadings(null, null, null, now)));
    }

    @Test
    void healthyWhenAllMetricsAreInRange() {
        var readings = new SensorReadings(50, 20.0, 55, now);
        assertEquals(GeneralHealthStatus.HEALTHY, evaluator.evaluate(readings));
    }

    @Test
    void warningWhenSoilMoistureIsModerate() {
        var readings = new SensorReadings(30, 20.0, 55, now);
        assertEquals(GeneralHealthStatus.WARNING, evaluator.evaluate(readings));
    }

    @Test
    void criticalWhenSoilMoistureIsBelowWiltingThreshold() {
        var readings = new SensorReadings(15, 20.0, 55, now);
        assertEquals(GeneralHealthStatus.CRITICAL, evaluator.evaluate(readings));
    }

    @Test
    void takesTheWorstMetricAcrossReadings() {
        // healthy moisture, but canopy wetness above the disease threshold -> critical.
        var readings = new SensorReadings(50, 20.0, 90, now);
        assertEquals(GeneralHealthStatus.CRITICAL, evaluator.evaluate(readings));
    }

    @Test
    void ignoresUnreportedMetrics() {
        // leaf-wetness-only device, leaf humidity healthy -> healthy overall.
        var readings = new SensorReadings(null, null, 50, now);
        assertEquals(GeneralHealthStatus.HEALTHY, evaluator.evaluate(readings));
    }
}
