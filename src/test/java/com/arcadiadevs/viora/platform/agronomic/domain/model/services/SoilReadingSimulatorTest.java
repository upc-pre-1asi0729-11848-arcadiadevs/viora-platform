package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoilReadingSimulatorTest {

    private final SoilReadingSimulator simulator = new SoilReadingSimulator();
    private final GeoPoint tacna = new GeoPoint(-18.0, -70.25);
    private final Instant now = Instant.parse("2026-06-29T15:00:00Z");

    @Test
    void isDeterministicForTheSameInputs() {
        var code = new ActivationCode("VIORA-WS01-3F9C");

        SensorReadings first = simulator.simulate(code, IoTDeviceType.WEATHER_STATION, tacna, 0.55, now);
        SensorReadings second = simulator.simulate(code, IoTDeviceType.WEATHER_STATION, tacna, 0.55, now);

        assertEquals(first, second);
    }

    @Test
    void soilProbeReportsSoilMetricsButNotLeafHumidity() {
        var code = new ActivationCode("VIORA-SP01-7K3M");

        SensorReadings readings = simulator.simulate(code, IoTDeviceType.SOIL_PROBE, tacna, 0.5, now);

        assertNotNull(readings.soilMoisture());
        assertNotNull(readings.soilTemperature());
        assertNull(readings.leafHumidity());
    }

    @Test
    void leafWetnessReportsOnlyLeafHumidity() {
        var code = new ActivationCode("VIORA-LW01-2H6T");

        SensorReadings readings = simulator.simulate(code, IoTDeviceType.LEAF_WETNESS, tacna, 0.5, now);

        assertNull(readings.soilMoisture());
        assertNull(readings.soilTemperature());
        assertNotNull(readings.leafHumidity());
    }

    @Test
    void keepsReadingsWithinPlausibleRanges() {
        var code = new ActivationCode("VIORA-WS02-6J2L");

        SensorReadings readings = simulator.simulate(code, IoTDeviceType.WEATHER_STATION, tacna, 0.6, now);

        assertTrue(readings.soilMoisture() >= 5 && readings.soilMoisture() <= 95);
        assertTrue(readings.leafHumidity() >= 20 && readings.leafHumidity() <= 99);
        assertTrue(readings.soilTemperature() >= -10.0 && readings.soilTemperature() <= 45.0);
    }

    @Test
    void higherNdviYieldsMoisterSoil() {
        var code = new ActivationCode("VIORA-SP02-9XQ2");

        SensorReadings dry = simulator.simulate(code, IoTDeviceType.SOIL_PROBE, tacna, 0.20, now);
        SensorReadings vigorous = simulator.simulate(code, IoTDeviceType.SOIL_PROBE, tacna, 0.80, now);

        assertTrue(vigorous.soilMoisture() > dry.soilMoisture());
    }

    @Test
    void toleratesMissingLocationAndNdvi() {
        var code = new ActivationCode("VIORA-WS03-1Z7Y");

        SensorReadings readings = simulator.simulate(code, IoTDeviceType.WEATHER_STATION, null, null, now);

        assertNotNull(readings.soilMoisture());
        assertNotNull(readings.capturedAt());
    }
}
