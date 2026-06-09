package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.AgronomicStatistic;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetAgronomicStatisticsQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DateRange;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.AgronomicStatisticRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for agronomic statistic read queries.
 *
 * <p>
 * Handles the retrieval of agronomic statistics by user, optional plot and time range.
 * It validates user ownership and plot ownership before querying the repository.
 * </p>
 */
@Service
public class AgronomicStatisticQueryService {

    private final AgronomicStatisticRepository agronomicStatisticRepository;
    private final PlotOwnershipValidator plotOwnershipValidator;

    /**
     * Creates a new AgronomicStatisticQueryService.
     *
     * @param agronomicStatisticRepository The agronomic statistic repository.
     * @param plotOwnershipValidator The plot ownership validator.
     */
    public AgronomicStatisticQueryService(
            AgronomicStatisticRepository agronomicStatisticRepository,
            PlotOwnershipValidator plotOwnershipValidator
    ) {
        this.agronomicStatisticRepository = agronomicStatisticRepository;
        this.plotOwnershipValidator = plotOwnershipValidator;
    }

    /**
     * Handles the query to get agronomic statistics.
     *
     * <p>
     * If the query has a plot id, the result is filtered by plot.
     * If the query does not have a plot id, the result is consolidated by user.
     * </p>
     *
     * @param query The query to get agronomic statistics.
     * @return A list of agronomic statistics.
     */
    public List<AgronomicStatistic> handle(GetAgronomicStatisticsQuery query) {
        this.validateAuthenticatedUser(query);

        DateRange dateRange = query.timeRange().toDateRange();

        if (query.plotId().isPresent()) {
            PlotId plotId = query.plotId().get();

            this.plotOwnershipValidator.validateOwnership(plotId, query.userId());

            return this.agronomicStatisticRepository.findAllByUserIdAndPlotIdAndMeasurementDateBetween(
                    query.userId(),
                    plotId,
                    dateRange
            );
        }

        return this.agronomicStatisticRepository.findAllByUserIdAndMeasurementDateBetween(
                query.userId(),
                dateRange
        );
    }

    /**
     * Validates that the authenticated user owns the requested data.
     *
     * @param query The query to validate.
     */
    private void validateAuthenticatedUser(GetAgronomicStatisticsQuery query) {
        if (!query.userId().equals(query.authenticatedUserId())) {
            throw new SecurityException("You are not allowed to access these agronomic statistics.");
        }
    }
}