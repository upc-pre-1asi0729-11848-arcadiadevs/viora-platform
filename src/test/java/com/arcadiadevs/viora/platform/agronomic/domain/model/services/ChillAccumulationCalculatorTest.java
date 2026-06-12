package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherReading;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.WeatherStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChillAccumulationCalculatorTest {

    private final ChillAccumulationCalculator calculator = new ChillAccumulationCalculator();

    @Test
    void countsChillingHoursAndAppliesUtahChillUnits() {
        var history = new WeatherHistory(List.of(
                reading(5.0),   // chilling hour; Utah +1.0
                reading(1.0),   // chilling hour; Utah 0.0 (<= 1.4)
                reading(20.0)   // warm; not chilling; Utah -1.0
        ));

        var dailyChill = calculator.computeDailyChill(history);

        assertEquals(2.0, dailyChill.chillHours());
        assertEquals(0.0, dailyChill.chillPortions());
    }

    @Test
    void warmDayProducesNegativeChillUnitsAndNoChillingHours() {
        var history = new WeatherHistory(List.of(
                reading(25.0),
                reading(19.0)
        ));

        var dailyChill = calculator.computeDailyChill(history);

        assertEquals(0.0, dailyChill.chillHours());
        assertEquals(-2.0, dailyChill.chillPortions());
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
