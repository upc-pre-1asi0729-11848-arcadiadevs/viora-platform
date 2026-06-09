package com.arcadiadevs.viora.platform.agronomic.application.ports;

import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import org.jspecify.annotations.NullMarked;

/**
 * Port interface for validating plot ownership.
 * Implementation is injected — not defined in this change.
 */
@NullMarked
public interface PlotOwnershipValidator {

    /**
     * Validates that the given user owns the specified plot.
     *
     * @param plotId the plot to validate ownership for
     * @param userId the user claiming ownership
     * @return Success if ownership is valid, Failure with PLOT_OWNERSHIP_VIOLATION otherwise
     */
    Result<Void, ApplicationError> validate(Long plotId, Long userId);
}
