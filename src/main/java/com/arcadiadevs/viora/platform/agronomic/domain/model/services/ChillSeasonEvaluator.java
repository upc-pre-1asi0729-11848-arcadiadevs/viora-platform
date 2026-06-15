package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Domain service that decides whether a plot is currently within the window in
 * which a winter-chill deficit is a meaningful phenological risk.
 *
 * <p>
 * Temperate fruit accumulate winter chill during dormancy; a deficit only
 * threatens the upcoming bloom from late dormancy through flowering. Outside
 * that window (e.g. summer fruit development) a low accumulated chill is the
 * normal off-season reading, not a current hazard, so the phenological
 * evaluator must not treat it as risk. The window is mirrored by hemisphere
 * from the plot's latitude.
 * </p>
 */
@Service
public class ChillSeasonEvaluator {

    /** Northern hemisphere: late dormancy through flowering (Jan–May). */
    static final int NORTHERN_WINDOW_START_MONTH = 1;
    static final int NORTHERN_WINDOW_END_MONTH = 5;
    /** Southern hemisphere: the same phase shifted ~6 months (Jul–Nov). */
    static final int SOUTHERN_WINDOW_START_MONTH = 7;
    static final int SOUTHERN_WINDOW_END_MONTH = 11;

    /**
     * @param latitudeDegrees Representative plot latitude (positive = north).
     * @param date The date to evaluate (typically today).
     * @return True when a chill deficit is phenologically relevant on that date.
     */
    public boolean isInChillRiskWindow(double latitudeDegrees, LocalDate date) {
        int month = date.getMonthValue();

        if (latitudeDegrees >= 0.0) {
            return month >= NORTHERN_WINDOW_START_MONTH && month <= NORTHERN_WINDOW_END_MONTH;
        }
        return month >= SOUTHERN_WINDOW_START_MONTH && month <= SOUTHERN_WINDOW_END_MONTH;
    }
}
