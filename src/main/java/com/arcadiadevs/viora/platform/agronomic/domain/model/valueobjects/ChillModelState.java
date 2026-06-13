package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Carry-over state of the Dynamic Model chill accumulation between ingestion
 * runs.
 *
 * <p>
 * Unlike additive models (Chilling Hours, Utah), the Dynamic Model only fixes a
 * chill portion once an intermediate metabolite crosses a threshold across a
 * continuous run of cold hours, with a partial reset afterwards. That
 * intermediate product therefore has to persist across the hour and day
 * boundaries of Viora's incremental, day-by-day snapshot pipeline; computing the
 * model independently per day would restart the metabolite at zero and grossly
 * under-count chill. This value object carries the standing intermediate product
 * and the last two temperatures required by the model's {@code xi[l-2]} reset
 * term, allowing exact continuation across ingestion windows.
 * </p>
 *
 * @param intermediateProduct The standing intermediate chilling product (x).
 * @param previousHourTemperatureCelsius Last processed hour's temperature, or
 *                                       null at the start of the season.
 * @param priorHourTemperatureCelsius Temperature immediately before the last
 *                                    processed hour, or null until two readings
 *                                    have been processed.
 */
public record ChillModelState(
        double intermediateProduct,
        Double previousHourTemperatureCelsius,
        Double priorHourTemperatureCelsius
) {

    /** State for a plot that has not accumulated any chill yet. */
    public static ChillModelState empty() {
        return new ChillModelState(0.0, null, null);
    }
}
