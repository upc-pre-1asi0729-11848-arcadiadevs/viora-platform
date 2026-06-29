package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Value object for an IoT device activation (claim) code.
 *
 * <p>
 * A producer claims a physical sensor by entering the code printed on its label.
 * The code's shape is {@code VIORA-<TT><NN>-<XXXX>} where {@code TT} encodes the
 * sensor kind (SP = soil probe, LW = leaf wetness, WS = weather station). This VO
 * guarantees the FORMAT; whether the code corresponds to a real issued unit is a
 * separate check against the {@code ActivationCodeCatalog}.
 * </p>
 */
public record ActivationCode(String value) {

    private static final Pattern PATTERN = Pattern.compile("^VIORA-(SP|LW|WS)\\d{2}-[A-Z0-9]{4}$");

    public ActivationCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Activation code is required");
        }
        value = value.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid activation code format '%s'. Expected VIORA-<SP|LW|WS><NN>-<XXXX>.".formatted(value));
        }
    }

    /**
     * Derives the sensor kind from the two-letter prefix that follows {@code VIORA-}.
     *
     * @return the device type encoded in the code
     */
    public IoTDeviceType deviceType() {
        String prefix = value.substring(6, 8);
        return switch (prefix) {
            case "SP" -> IoTDeviceType.SOIL_PROBE;
            case "LW" -> IoTDeviceType.LEAF_WETNESS;
            case "WS" -> IoTDeviceType.WEATHER_STATION;
            default -> throw new IllegalStateException("Unreachable: prefix validated by the pattern");
        };
    }
}
