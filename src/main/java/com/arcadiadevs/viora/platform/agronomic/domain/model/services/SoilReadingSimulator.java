package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.GeoPoint;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceType;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.SensorReadings;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Domain service that produces a device's current soil/canopy telemetry.
 *
 * <p>
 * Viora has no physical sensor hardware, so readings are <b>simulated</b> rather
 * than ingested. The simulation is a pure, deterministic function of the device's
 * activation code (per-device seed), the plot's geography (latitude → hemisphere,
 * climate band and seasonal swing; longitude → local solar hour), the time of day,
 * the season, and the plot's latest NDVI (canopy vigor → water status nudge). It
 * performs no external calls and no persistence, so it never adds backend load:
 * the values are computed on read.
 * </p>
 *
 * <p>
 * Because it is seeded by the activation code, a given device reads consistently
 * and evolves smoothly across the day (hotter, drier afternoons; cooler, moister
 * nights; wetter soil on vigorous plots), which keeps the dashboard coherent with
 * the plot's real season and location.
 * </p>
 */
@Service
public class SoilReadingSimulator {

    /**
     * Produces the current readings for a device.
     *
     * @param code       the device activation code (per-device seed); may be null
     * @param type       the sensor kind, which decides the reported metrics
     * @param location   the plot's representative point (centroid); may be null
     * @param latestNdvi the plot's most recent NDVI (canopy vigor), or null
     * @param now        the reference instant
     * @return the simulated readings, with unreported metrics left null
     */
    public SensorReadings simulate(
            ActivationCode code,
            IoTDeviceType type,
            GeoPoint location,
            Double latestNdvi,
            Instant now) {

        long seed = code != null ? code.value().hashCode() : 0L;
        double latitude = location != null ? location.getLatitude() : 0.0;
        double longitude = location != null ? location.getLongitude() : 0.0;

        LocalDateTime utc = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        int dayOfYear = utc.getDayOfYear();
        double localHour = Math.floorMod(Math.round(utc.getHour() + longitude / 15.0), 24L);

        // Seasonal warmth: positive in the local hemisphere's summer. Day 80 ≈ the
        // March equinox, so the northern peak lands near the June solstice.
        double hemisphere = latitude >= 0 ? 1.0 : -1.0;
        double seasonal = Math.sin(2 * Math.PI * (dayOfYear - 80) / 365.0) * hemisphere; // [-1, 1]
        double warmth01 = (seasonal + 1) / 2.0;

        // Diurnal warmth: peaks mid-afternoon (~15h), bottoms before dawn (~3h).
        double diurnal = Math.sin(2 * Math.PI * (localHour - 9) / 24.0); // [-1, 1]
        double diurnal01 = (diurnal + 1) / 2.0;

        double absLat = Math.abs(latitude);
        double annualMean = clamp(27.0 - 0.45 * absLat, -5.0, 30.0);
        double seasonalAmplitude = 2.0 + 0.30 * absLat;
        double diurnalAmplitude = 6.0;

        double airTemperature = annualMean
                + seasonalAmplitude * seasonal
                + diurnalAmplitude * diurnal
                + 1.5 * unitOffset(seed, 11);

        // Soil temperature lags and dampens the air temperature.
        double soilTemperature = annualMean + 0.6 * (airTemperature - annualMean);

        // Soil moisture falls with heat/sun; vigorous canopy (high NDVI) reads moister.
        double dryness = 0.6 * warmth01 + 0.4 * diurnal01; // [0, 1]
        double moisture = 55.0 - 30.0 * dryness;
        if (latestNdvi != null) {
            moisture += clamp((latestNdvi - 0.4) * 25.0, -8.0, 12.0);
        }
        moisture += 5.0 * unitOffset(seed, 23);
        int soilMoisture = (int) Math.round(clamp(moisture, 5.0, 95.0));

        // Leaf humidity is high overnight/at dawn and tracks soil water status.
        double leaf = 90.0 - 45.0 * diurnal01 + (moisture - 40.0) * 0.2 + 5.0 * unitOffset(seed, 31);
        int leafHumidity = (int) Math.round(clamp(leaf, 20.0, 99.0));

        // Stagger the "last reading" a little so devices don't all share a timestamp.
        Instant capturedAt = now.minusSeconds(Math.floorMod(seed, 180L) + 30L);

        return new SensorReadings(
                type.reportsSoilMoisture() ? soilMoisture : null,
                type.reportsSoilTemperature() ? round1(soilTemperature) : null,
                type.reportsLeafHumidity() ? leafHumidity : null,
                capturedAt);
    }

    /** Deterministic per-device offset in [-1, 1), salted so metrics differ. */
    private static double unitOffset(long seed, int salt) {
        double s = Math.sin((seed * 31L + salt) * 12.9898) * 43758.5453;
        return (s - Math.floor(s)) * 2.0 - 1.0;
    }

    private static double clamp(double value, double low, double high) {
        return Math.max(low, Math.min(high, value));
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
