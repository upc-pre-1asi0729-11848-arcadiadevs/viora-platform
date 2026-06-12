package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviHistory;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NdviTrendDirection;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NdviTrendAnalyzerTest {

    private final NdviTrendAnalyzer analyzer = new NdviTrendAnalyzer();

    @Test
    void classifiesRisingTrendAndComputesChangeRate() {
        var trend = analyzer.analyze(history(0.40, 0.62));

        assertEquals(NdviTrendDirection.RISING, trend.direction());
        assertEquals(0.22, trend.changeRate(), 1e-9);
    }

    @Test
    void classifiesFallingTrend() {
        var trend = analyzer.analyze(history(0.70, 0.55));

        assertEquals(NdviTrendDirection.FALLING, trend.direction());
    }

    @Test
    void treatsSmallFluctuationAsStable() {
        var trend = analyzer.analyze(history(0.500, 0.515));

        assertEquals(NdviTrendDirection.STABLE, trend.direction());
    }

    private NdviHistory history(double earliestMean, double latestMean) {
        return new NdviHistory(List.of(
                new NdviStatistic(Instant.parse("2026-04-01T00:00:00Z"), earliestMean, null, null, null, null, null, null),
                new NdviStatistic(Instant.parse("2026-06-01T00:00:00Z"), latestMean, null, null, null, null, null, null)
        ));
    }
}
