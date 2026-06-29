package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivationCodeTest {

    @Test
    void normalizesToUpperCaseAndTrims() {
        assertEquals("VIORA-SP01-7K3M", new ActivationCode("  viora-sp01-7k3m  ").value());
    }

    @Test
    void derivesSoilProbeFromSpPrefix() {
        assertEquals(IoTDeviceType.SOIL_PROBE, new ActivationCode("VIORA-SP02-9XQ2").deviceType());
    }

    @Test
    void derivesLeafWetnessFromLwPrefix() {
        assertEquals(IoTDeviceType.LEAF_WETNESS, new ActivationCode("VIORA-LW01-2H6T").deviceType());
    }

    @Test
    void derivesWeatherStationFromWsPrefix() {
        assertEquals(IoTDeviceType.WEATHER_STATION, new ActivationCode("VIORA-WS03-1Z7Y").deviceType());
    }

    @Test
    void rejectsBlankCode() {
        assertThrows(IllegalArgumentException.class, () -> new ActivationCode("  "));
    }

    @Test
    void rejectsUnknownPrefix() {
        assertThrows(IllegalArgumentException.class, () -> new ActivationCode("VIORA-XX01-7K3M"));
    }

    @Test
    void rejectsMalformedShape() {
        assertThrows(IllegalArgumentException.class, () -> new ActivationCode("SP01-7K3M"));
    }
}
