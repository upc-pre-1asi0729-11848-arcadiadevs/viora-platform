package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillModelState;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import org.springframework.stereotype.Service;

/**
 * Domain service that accumulates winter chill from hourly weather.
 *
 * <p>
 * Chill <em>portions</em> use the Dynamic Model (Fishman &amp; Erez 1987), the
 * model whose unit is the Chill Portion (CP) and the agronomic standard for warm
 * climates such as the olive-growing valleys Viora targets. It is implemented
 * with the formulation extracted by Luedeling et al. (2009) and used by the
 * {@code chillR} package, with the standard constants {@code E0=4153.5,
 * E1=12888.8, A0=139500, A1=2.567e18, slope=1.6, Tf=277}. Chill <em>hours</em>
 * keep the classic Chilling Hours model (hours within {@code [0, 7.2] °C}).
 * Each weather reading is treated as one hour, matching the provider's history.
 * </p>
 *
 * <p>
 * The Dynamic Model is stateful: a portion is only fixed once an intermediate
 * metabolite builds up across a continuous run of cold hours and crosses a
 * threshold, then partially resets. Because Viora ingests weather one day at a
 * time, the intermediate product is carried between runs via {@link ChillModelState}
 * so the seasonal accumulation stays continuous; computing the model per day in
 * isolation would restart the metabolite at zero and badly under-count chill.
 * </p>
 *
 * <p>
 * These are established, citable models used as a first approximation; the
 * results should be validated by an agronomist before driving production
 * agronomic decisions, consistent with the project's stance on agronomic
 * defaults.
 * </p>
 */
@Service
public class ChillAccumulationCalculator {

    static final double CHILLING_HOURS_LOWER_CELSIUS = 0.0;
    static final double CHILLING_HOURS_UPPER_CELSIUS = 7.2;

    // Dynamic Model constants (Fishman & Erez 1987; Luedeling et al. 2009 / chillR).
    private static final double E0 = 4153.5;
    private static final double E1 = 12888.8;
    private static final double A0 = 139500.0;
    private static final double A1 = 2.567e18;
    private static final double SLOPE = 1.6;
    private static final double TF = 277.0;
    private static final double AA = A0 / A1;
    private static final double EE = E1 - E0;
    private static final double CELSIUS_TO_KELVIN = 273.0;

    /**
     * Accumulates the chill contributed by a window of hourly weather, continuing
     * the Dynamic Model from a previous carry-over state.
     *
     * @param history The window's hourly weather readings.
     * @param incomingState The Dynamic Model state carried from the prior window.
     * @return The chill hours and chill portions added by this window, plus the
     *         updated state to carry forward.
     */
    public ChillAccumulation accumulate(WeatherHistory history, ChillModelState incomingState) {
        if (history == null) {
            throw new IllegalArgumentException("Weather history is required to compute chill.");
        }
        var state = incomingState == null ? ChillModelState.empty() : incomingState;

        double chillHours = 0.0;
        double chillPortions = 0.0;
        double intermediate = state.intermediateProduct();
        Double fromTemperature = state.previousHourTemperatureCelsius();
        // Temperature of the hour preceding the current "from" hour; conditions the
        // reset term (chillR's xi[l-2]). Null only at a window seam, where we fall
        // back to the from hour's own xi — a negligible once-per-window lag.
        Double priorTemperature = null;

        for (var reading : history.readings()) {
            double temperature = reading.temperatureCelsius();

            if (temperature >= CHILLING_HOURS_LOWER_CELSIUS
                    && temperature <= CHILLING_HOURS_UPPER_CELSIUS) {
                chillHours += 1.0;
            }

            if (fromTemperature == null) {
                // First hour of the whole accumulation: establish the landing point.
                fromTemperature = temperature;
                continue;
            }

            double fromXs = xs(fromTemperature);
            double fromEak1 = eak1(fromTemperature);
            double fromXi = xi(fromTemperature);
            double resetXi = priorTemperature == null ? fromXi : xi(priorTemperature);

            double carried = intermediate >= 1.0 ? intermediate * (1.0 - resetXi) : intermediate;
            double next = fromXs - (fromXs - carried) * fromEak1;
            if (next >= 1.0) {
                chillPortions += next * fromXi;
            }

            intermediate = next;
            priorTemperature = fromTemperature;
            fromTemperature = temperature;
        }

        return new ChillAccumulation(
                chillHours,
                chillPortions,
                new ChillModelState(intermediate, fromTemperature)
        );
    }

    private double tk(double temperatureCelsius) {
        return temperatureCelsius + CELSIUS_TO_KELVIN;
    }

    /** Fraction of the intermediate product that converts to a fixed portion. */
    private double xi(double temperatureCelsius) {
        double sr = Math.exp(SLOPE * TF * (tk(temperatureCelsius) - TF) / tk(temperatureCelsius));
        return sr / (1.0 + sr);
    }

    /** Equilibrium level of the intermediate product at the given temperature. */
    private double xs(double temperatureCelsius) {
        return AA * Math.exp(EE / tk(temperatureCelsius));
    }

    /** Per-hour decay factor of the intermediate product toward equilibrium. */
    private double eak1(double temperatureCelsius) {
        return Math.exp(-A1 * Math.exp(-E1 / tk(temperatureCelsius)));
    }

    /**
     * Chill accumulated over a window of hourly weather.
     *
     * @param chillHours Chilling hours added by the window.
     * @param chillPortions Dynamic Model chill portions added by the window.
     * @param newState The Dynamic Model state to carry into the next window.
     */
    public record ChillAccumulation(
            double chillHours,
            double chillPortions,
            ChillModelState newState
    ) {
    }
}
