package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * Lifecycle/traceability status of the expense record. Manually registered costs
 * are {@code REGISTERED}; costs derived from a confirmed-alert intervention (e.g.
 * an accepted specialist proposal) are {@code ALERT_CONFIRMED}.
 */
public enum ExpenseStatus {
    REGISTERED,
    ALERT_CONFIRMED
}
