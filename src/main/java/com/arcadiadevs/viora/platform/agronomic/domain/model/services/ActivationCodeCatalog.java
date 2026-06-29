package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ActivationCode;

/**
 * Catalog of activation codes that correspond to real issued sensor units.
 *
 * <p>
 * Mirrors a manufacturer/distributor registry: a producer can only claim a device
 * whose code was actually issued. The format is enforced by {@link ActivationCode};
 * this port answers whether such a code exists in the issued set.
 * </p>
 */
public interface ActivationCodeCatalog {

    /**
     * @param code a well-formed activation code
     * @return true if the code corresponds to an issued device unit
     */
    boolean isIssued(ActivationCode code);
}
