package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.DynamicNutritionPlanAssemblerService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanUnavailableException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.RecommendDynamicNutritionCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.DynamicNutritionPlanRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for commands over the DynamicNutritionPlan aggregate.
 *
 * <p>
 * Handles RecommendDynamicNutritionCommand by validating plot ownership,
 * superseding any previously active plan and persisting the newly
 * recommended plan.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DynamicNutritionPlanCommandService {

    private final PlotOwnershipValidator plotOwnershipValidator;
    private final DynamicNutritionPlanRepository dynamicNutritionPlanRepository;
    private final DynamicNutritionPlanAssemblerService dynamicNutritionPlanAssemblerService;

    /**
     * Recommends a new dynamic nutrition plan for the specified plot.
     *
     * @param command the recommendation command with userId and plotId
     * @return Success with the created plan, or Failure when the plot is not
     *         found, not owned by the user, or the plan cannot be generated
     */
    @Transactional
    public Result<DynamicNutritionPlan, ApplicationError> handle(RecommendDynamicNutritionCommand command) {
        try {
            var userId = new UserId(command.userId());
            var plotId = new PlotId(command.plotId());

            return plotOwnershipValidator.validate(userId, plotId)
                    .flatMap(plot -> recommendForPlot(plot, userId, plotId));

        } catch (DynamicNutritionPlanUnavailableException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "dynamic-nutrition-plan-availability",
                    exception.getMessage()
            ));
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "recommend-dynamic-nutrition-command",
                    exception.getMessage()
            ));
        }
    }

    private Result<DynamicNutritionPlan, ApplicationError> recommendForPlot(
            Plot plot,
            UserId userId,
            PlotId plotId
    ) {
        var plan = dynamicNutritionPlanAssemblerService.assembleForPlot(plot);

        dynamicNutritionPlanRepository.findActiveByUserIdAndPlotId(userId, plotId)
                .ifPresent(previousPlan -> {
                    previousPlan.supersede();
                    dynamicNutritionPlanRepository.save(previousPlan);
                });

        return Result.success(dynamicNutritionPlanRepository.save(plan));
    }
}
