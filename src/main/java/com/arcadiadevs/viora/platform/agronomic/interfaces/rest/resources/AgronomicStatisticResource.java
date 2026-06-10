package com.arcadiadevs.viora.platform.agronomic.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Agronomic statistic resource.
 *
 * <p>
 * Represents agronomic metrics exposed to the frontend
 * to graph NDVI, chill portions and chill hours trends.
 * </p>
 *
 * @param measurementDate The date when the metric was measured.
 * @param ndviValue The NDVI value.
 * @param chillPortions The chill portions value.
 * @param chillHours The accumulated chill hours value.
 */
public record AgronomicStatisticResource(
        LocalDate measurementDate,
        Double ndviValue,
        Double chillPortions,
        Double chillHours
) {
}