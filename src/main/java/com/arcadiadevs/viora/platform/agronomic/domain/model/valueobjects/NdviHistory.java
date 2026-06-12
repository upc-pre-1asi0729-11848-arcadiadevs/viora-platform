package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

import java.util.Comparator;
import java.util.List;

/**
 * Historical NDVI statistics for a plot over a requested range.
 *
 * @param statistics NDVI statistics ordered by timestamp; never empty.
 */
public record NdviHistory(
        List<NdviStatistic> statistics
) {
    public NdviHistory {
        if (statistics == null || statistics.isEmpty()) {
            throw new IllegalArgumentException("NDVI history must contain at least one statistic.");
        }
        statistics = List.copyOf(statistics).stream()
                .sorted(Comparator.comparing(NdviStatistic::timestamp))
                .toList();
    }

    /** Most recent NDVI statistic in the series. */
    public NdviStatistic latest() {
        return statistics.getLast();
    }
}
