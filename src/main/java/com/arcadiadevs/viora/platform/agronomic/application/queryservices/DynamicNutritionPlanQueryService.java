package com.arcadiadevs.viora.platform.agronomic.application.queryservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanNotFoundException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.queries.GetActiveDynamicNutritionPlanQuery;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.DynamicNutritionPlanRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dynamic nutrition plan query service.
 *
 * <p>
 * Handles read operations related to dynamic nutrition plans,
 * validating plot ownership before exposing the active plan.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DynamicNutritionPlanQueryService {

    private final PlotOwnershipValidator plotOwnershipValidator;
    private final DynamicNutritionPlanRepository dynamicNutritionPlanRepository;

    /**
     * Handles the GetActiveDynamicNutritionPlanQuery to retrieve the active plan for a plot.
     *
     * @param query The query containing the user and plot identifiers.
     * @return Success with the active plan, or Failure when the plot is invalid,
     *         not owned by the user, or no active plan exists
     */
    @Transactional(readOnly = true)
    public Result<DynamicNutritionPlan, ApplicationError> handle(GetActiveDynamicNutritionPlanQuery query) {
        try {
            var userId = new UserId(query.userId());
            var plotId = new PlotId(query.plotId());

            return plotOwnershipValidator.validate(userId, plotId)
                    .flatMap(plot -> findActivePlan(userId, plotId, query.plotId()));

        } catch (DynamicNutritionPlanNotFoundException exception) {
            return Result.failure(ApplicationError.notFound(
                    "Dynamic_nutrition_plan",
                    String.valueOf(query.plotId())
            ));
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "get-active-dynamic-nutrition-plan-query",
                    exception.getMessage()
            ));
        }
    }

    private Result<DynamicNutritionPlan, ApplicationError> findActivePlan(
            UserId userId,
            PlotId plotId,
            Long rawPlotId
    ) {
        var plan = dynamicNutritionPlanRepository.findActiveByUserIdAndPlotId(userId, plotId)
                .orElseThrow(() -> new DynamicNutritionPlanNotFoundException(rawPlotId));

        return Result.success(plan);
    }
}
