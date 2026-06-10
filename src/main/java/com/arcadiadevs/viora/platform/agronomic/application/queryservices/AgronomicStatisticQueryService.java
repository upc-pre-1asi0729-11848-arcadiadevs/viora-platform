package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticsQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.PlotRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Agronomic statistic query service.
 *
 * <p>
 * Handles read operations related to agronomic statistics,
 * validating user ownership and resolving time ranges into date ranges.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AgronomicStatisticQueryService {

    private final AgronomicStatisticRepository agronomicStatisticRepository;

    private final PlotRepository plotRepository;

    @Transactional(readOnly = true)
    public Result<List<AgronomicStatistic>, ApplicationError> handle(
            GetAgronomicStatisticsQuery query
    ) {
        try {
            var userId = new UserId(query.userId());
            var authenticatedUserId = new UserId(query.authenticatedUserId());

            if (!userId.equals(authenticatedUserId)) {
                return Result.failure(ApplicationError.forbidden(
                        "agronomic-statistics-access",
                        "Authenticated user cannot access statistics from another user."
                ));
            }

            var dateRange = query.timeRange().toDateRange(LocalDate.now());

            if (query.plotId() == null) {
                var statistics = agronomicStatisticRepository
                        .findAllByUserIdAndMeasurementDateBetween(userId, dateRange);

                return Result.success(statistics);
            }

            var plotId = new PlotId(query.plotId());
            var plot = plotRepository.findById(plotId);

            if (plot.isEmpty() || !plot.get().isActive()) {
                return Result.failure(ApplicationError.validationError(
                        "plotId",
                        "The selected plot does not exist or is inactive."
                ));
            }

            if (!plot.get().belongsTo(userId)) {
                return Result.failure(ApplicationError.forbidden(
                        "plot-ownership",
                        "User %d does not own plot %d.".formatted(
                                query.userId(),
                                query.plotId()
                        )
                ));
            }

            var statistics = agronomicStatisticRepository
                    .findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                            userId,
                            plotId,
                            dateRange
                    );

            return Result.success(statistics);

        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "agronomic-statistics-query",
                    exception.getMessage()
            ));
        }
    }
}