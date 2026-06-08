package com.arcadiadevs.viora.platform.agronomic.domain.repositories;

/**
 * Minimal domain repository contract for the Plot aggregate.
 * Exposes only the ownership check required by IoT device services.
 */
public interface PlotRepository {

    boolean existsByIdAndOwnerUserId(Long plotId, Long ownerUserId);
}
