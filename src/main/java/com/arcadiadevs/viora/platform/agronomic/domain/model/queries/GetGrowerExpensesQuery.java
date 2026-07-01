package com.arcadiadevs.viora.platform.agronomic.domain.model.queries;

/**
 * Query to list a grower's expenses, optionally scoped to a single plot. When
 * {@code plotId} is null the grower's full expense history is returned.
 */
public record GetGrowerExpensesQuery(Long growerId, Long plotId) {
    public GetGrowerExpensesQuery {
        if (growerId == null || growerId <= 0) {
            throw new IllegalArgumentException("Grower ID must be a positive number.");
        }
        if (plotId != null && plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be a positive number when provided.");
        }
    }
}
