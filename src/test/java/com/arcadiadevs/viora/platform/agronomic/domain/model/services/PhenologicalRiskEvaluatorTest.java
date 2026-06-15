package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ClimateRiskLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhenologicalRiskEvaluatorTest {

    private final PhenologicalRiskEvaluator evaluator = new PhenologicalRiskEvaluator();

    @Test
    void returnsUnknownWhenChillIsMissing() {
        assertEquals(ClimateRiskLevel.UNKNOWN, evaluator.evaluate(null, 40.0, null, false, true));
    }

    @Test
    void returnsUnknownWhenRequirementIsNotPositive() {
        assertEquals(ClimateRiskLevel.UNKNOWN, evaluator.evaluate(20.0, 0.0, null, false, true));
    }

    @Test
    void severeChillDeficitInWindowIsHigh() {
        // 8 / 40 = 0.2 ratio, below the severe threshold, within the chill window.
        assertEquals(ClimateRiskLevel.HIGH, evaluator.evaluate(8.0, 40.0, null, false, true));
    }

    @Test
    void warmAnomalyEscalatesPartialDeficitToHigh() {
        // 24 / 40 = 0.6 (moderate band) compounded by a +3.5 C warm anomaly, in window.
        assertEquals(ClimateRiskLevel.HIGH, evaluator.evaluate(24.0, 40.0, 3.5, false, true));
    }

    @Test
    void partialChillDeficitInWindowIsModerate() {
        // 20 / 40 = 0.5 ratio, within the moderate band and the chill window.
        assertEquals(ClimateRiskLevel.MODERATE, evaluator.evaluate(20.0, 40.0, null, false, true));
    }

    @Test
    void fallingNdviRaisesOtherwiseLowToModerate() {
        // 36 / 40 = 0.9 ratio (would be low) but NDVI is declining (any season).
        assertEquals(ClimateRiskLevel.MODERATE, evaluator.evaluate(36.0, 40.0, null, true, true));
    }

    @Test
    void fulfilledChillIsLow() {
        // 36 / 40 = 0.9 ratio, no anomaly, stable NDVI.
        assertEquals(ClimateRiskLevel.LOW, evaluator.evaluate(36.0, 40.0, 0.5, false, true));
    }

    @Test
    void chillDeficitOutsideWindowIsLow() {
        // Same severe deficit as above, but off-season: a low chill reading is the
        // normal state, so it must not raise risk.
        assertEquals(ClimateRiskLevel.LOW, evaluator.evaluate(8.0, 40.0, null, false, false));
    }

    @Test
    void warmAnomalyOutsideWindowStillModerate() {
        // Off-season chill is ignored, but a warm anomaly remains a moderate signal.
        assertEquals(ClimateRiskLevel.MODERATE, evaluator.evaluate(8.0, 40.0, 2.5, false, false));
    }
}
