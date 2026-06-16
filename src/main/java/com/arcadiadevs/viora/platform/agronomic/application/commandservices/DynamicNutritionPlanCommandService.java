package com.arcadiadevs.viora.platform.agronomic.application.commandservices;

import com.arcadiadevs.viora.platform.agronomic.application.internal.DynamicNutritionPlanAssemblerService;
import com.arcadiadevs.viora.platform.agronomic.application.internal.PlotOwnershipValidator;
import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanUnavailableException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.DynamicNutritionPlan;
import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.CertifyNutritionApplicationCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.commands.RecommendDynamicNutritionCommand;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DoseConfirmation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplication;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.agronomic.domain.repositories.DynamicNutritionPlanRepository;
import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import com.arcadiadevs.viora.platform.agronomic.interfaces.events.DynamicNutritionPlanGeneratedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

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
                    .flatMap(plot -> recommendForPlot(plot, userId, plotId, command.alertId()));

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

    /**
     * Certifies the in-field execution of a dynamic nutrition plan.
     *
     * @param command The certification command.
     * @return Success with the certified plan, or Failure when the plan is not
     *         found, not owned by the user, or cannot be certified.
     */
    @Transactional
    public Result<DynamicNutritionPlan, ApplicationError> handle(CertifyNutritionApplicationCommand command) {
        try {
            var planOptional = dynamicNutritionPlanRepository.findById(new DynamicNutritionPlanId(command.planId()));
            if (planOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "dynamic-nutrition-plan",
                        command.planId().toString()
                ));
            }

            var plan = planOptional.get();
            if (!plan.getUserId().getValue().equals(command.userId())) {
                return Result.failure(ApplicationError.forbidden(
                        "dynamic-nutrition-plan-ownership",
                        "User %d does not own dynamic nutrition plan %d.".formatted(
                                command.userId(),
                                command.planId()
                        )
                ));
            }

            var application = new NutritionApplication(
                    command.applicationDate(),
                    command.applicationTime(),
                    command.appliedInputs(),
                    DoseConfirmation.fromString(command.doseConfirmation()),
                    command.fieldOperator(),
                    command.fieldNotes()
            );

            plan.certifyApplication(application);

            return Result.success(dynamicNutritionPlanRepository.save(plan));

        } catch (DynamicNutritionPlanUnavailableException exception) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "dynamic-nutrition-plan-certification",
                    exception.getMessage()
            ));
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(
                    "certify-nutrition-application-command",
                    exception.getMessage()
            ));
        }
    }

    private Result<DynamicNutritionPlan, ApplicationError> recommendForPlot(
            Plot plot,
            UserId userId,
            PlotId plotId,
            Long alertId
    ) {
        var plan = dynamicNutritionPlanAssemblerService.assembleForPlot(plot);

        dynamicNutritionPlanRepository.findActiveByUserIdAndPlotId(userId, plotId)
                .ifPresent(previousPlan -> {
                    previousPlan.supersede();
                    dynamicNutritionPlanRepository.save(previousPlan);
                });

        var savedPlan = dynamicNutritionPlanRepository.save(plan);
        if (alertId != null) {
            eventPublisher.publishEvent(new DynamicNutritionPlanGeneratedIntegrationEvent(
                    this,
                    savedPlan.getId().getValue(),
                    plotId.getValue(),
                    alertId
            ));
        }

        return Result.success(savedPlan);
    }
}
