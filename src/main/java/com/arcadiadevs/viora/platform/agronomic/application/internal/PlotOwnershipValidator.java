package com.arcadiadevs.viora.platform.agronomic.application.internal;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Validates that an active plot exists and belongs to a given user.
 */
@Service
@RequiredArgsConstructor
public class PlotOwnershipValidator {

    private final PlotRepository plotRepository;

    /**
     * Resolves an active plot and verifies its ownership.
     *
     * @param userId Expected owner identifier.
     * @param plotId Plot identifier.
     * @return The active owned plot, or the corresponding application error.
     */
    public Result<Plot, ApplicationError> validate(UserId userId, PlotId plotId) {
        var plot = plotRepository.findById(plotId);

        if (plot.isEmpty() || !plot.get().isActive()) {
            return Result.failure(ApplicationError.notFound(
                    "Plot",
                    String.valueOf(plotId.getValue())
            ));
        }

        if (!plot.get().belongsTo(userId)) {
            return Result.failure(ApplicationError.forbidden(
                    "plot-ownership",
                    "User %d does not own plot %d.".formatted(
                            userId.getValue(),
                            plotId.getValue()
                    )
            ));
        }

        return Result.success(plot.get());
    }
}
