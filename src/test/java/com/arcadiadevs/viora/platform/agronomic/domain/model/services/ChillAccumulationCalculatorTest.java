package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillModelState;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChillAccumulationCalculatorTest {

    private final ChillAccumulationCalculator calculator = new ChillAccumulationCalculator();

    @Test
    void countsChillingHoursWithinTheRange() {
        var history = new WeatherHistory(List.of(
                reading(5.0),    // chilling hour
                reading(1.0),    // chilling hour
                reading(20.0)    // warm; not a chilling hour
        ));

        var accumulation = calculator.accumulate(history, ChillModelState.empty());

        assertEquals(2.0, accumulation.chillHours());
    }

    @Test
    void warmWeatherFixesNoChillPortions() {
        var accumulation = calculator.accumulate(constant(25.0, 48), ChillModelState.empty());

        assertEquals(0.0, accumulation.chillPortions());
        assertEquals(0.0, accumulation.chillHours());
    }

    @Test
    void sustainedColdAccumulatesChillPortionsAndMoreColdAccumulatesMore() {
        var shortCold = calculator.accumulate(constant(5.0, 100), ChillModelState.empty());
        var longCold = calculator.accumulate(constant(5.0, 400), ChillModelState.empty());

        assertTrue(shortCold.chillPortions() > 0.0, "sustained cold should fix chill portions");
        assertTrue(longCold.chillPortions() > shortCold.chillPortions(), "more cold hours should fix more portions");
    }

    @Test
    void carriesStateSoSplitAccumulationMatchesContinuousAccumulation() {
        var continuous = calculator.accumulate(constant(5.0, 200), ChillModelState.empty());

        var first = calculator.accumulate(constant(5.0, 100), ChillModelState.empty());
        var second = calculator.accumulate(constant(5.0, 100), first.newState());
        double split = first.chillPortions() + second.chillPortions();

        assertEquals(continuous.chillPortions(), split, 1e-9);
    }

    private WeatherHistory constant(double temperatureCelsius, int hours) {
        var readings = new ArrayList<WeatherReading>(hours);
        for (int i = 0; i < hours; i++) {
            readings.add(reading(temperatureCelsius));
        }
        return new WeatherHistory(readings);
    }

    private WeatherReading reading(double temperatureCelsius) {
        return new WeatherReading(
                Instant.parse("2026-06-11T00:00:00Z"),
                temperatureCelsius,
                WeatherStatus.UNKNOWN,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
