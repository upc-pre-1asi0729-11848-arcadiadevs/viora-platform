package com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects;

/**
 * The kind of operational cost an expense represents, mirroring the two response
 * flows it can be linked to: climate/nutrition mitigation or a phytosanitary
 * (pest) intervention.
 */
public enum ExpenseType {
    CLIMATE_MITIGATION,
    PEST_INTERVENTION
}
