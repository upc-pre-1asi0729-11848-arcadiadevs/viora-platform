package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.YieldEstimationPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YieldForecastEstimatorTest {

    // base 4.0 t/ha, ndvi floor 0.2, optimal 0.8, chill requirement 40, chill min factor 0.6.
    private final YieldForecastEstimator estimator =
            new YieldForecastEstimator(new YieldEstimationPolicy(4.0, 0.20, 0.80, 40.0, 0.60));

    @Test
    void scalesYieldByVigorChillAdequacyAndArea() {
        // vigor (0.62-0.2)/0.6 = 0.7; chill 45/40 -> 1.0 -> modifier 1.0; 4.0*0.7*1.0*12.5 = 35.0
        var yield = estimator.estimate(0.62, 45.0, 12.5);

        assertEquals(35.0, yield.getValue(), 1e-6);
    }

    @Test
    void appliesMinimumChillModifierWhenChillIsAbsent() {
        // vigor 0.7; chill 0 -> modifier 0.6; 4.0*0.7*0.6*10 = 16.8
        var yield = estimator.estimate(0.62, 0.0, 10.0);

        assertEquals(16.8, yield.getValue(), 1e-6);
    }

    @Test
    void yieldsZeroWhenVigorIsAtOrBelowTheFloor() {
        var yield = estimator.estimate(0.15, 50.0, 10.0);

        assertEquals(0.0, yield.getValue(), 1e-6);
    }

    @Test
    void capsVigorAndChillContributionsAtOptimum() {
        // vigor capped at 1.0, chill capped at 1.0 -> 4.0*1.0*1.0*5 = 20.0
        var yield = estimator.estimate(0.95, 100.0, 5.0);

        assertEquals(20.0, yield.getValue(), 1e-6);
        assertTrue(yield.getValue() > 0);
    }
}
