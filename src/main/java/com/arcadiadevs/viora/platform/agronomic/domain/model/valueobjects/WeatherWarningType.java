package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Category of an agronomic weather warning derived from the forecast.
 */
public enum WeatherWarningType {

    /** Risk of frost damage from low minimum temperatures. */
    FROST,

    /** Risk of heat stress from high maximum temperatures. */
    HEAT_STRESS,

    /** Thunderstorm conditions in the forecast. */
    STORM,

    /** Damaging wind gusts. */
    HIGH_WIND,

    /** Heavy precipitation that may cause waterlogging or runoff. */
    HEAVY_RAIN
}
