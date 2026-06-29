package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Kind of physical IoT sensor, encoded in the device's activation-code prefix.
 *
 * <p>
 * Each type reports a fixed set of soil/canopy metrics, mirroring how real field
 * hardware is specialized:
 * <ul>
 *   <li>{@code SOIL_PROBE} — soil moisture + soil temperature</li>
 *   <li>{@code LEAF_WETNESS} — leaf humidity</li>
 *   <li>{@code WEATHER_STATION} — all three</li>
 * </ul>
 * </p>
 */
public enum IoTDeviceType {
    SOIL_PROBE,
    LEAF_WETNESS,
    WEATHER_STATION;

    /** Whether this sensor reports soil moisture (soil probes + weather stations). */
    public boolean reportsSoilMoisture() {
        return this != LEAF_WETNESS;
    }

    /** Whether this sensor reports soil temperature (soil probes + weather stations). */
    public boolean reportsSoilTemperature() {
        return this != LEAF_WETNESS;
    }

    /** Whether this sensor reports leaf humidity (leaf-wetness sensors + weather stations). */
    public boolean reportsLeafHumidity() {
        return this != SOIL_PROBE;
    }
}
