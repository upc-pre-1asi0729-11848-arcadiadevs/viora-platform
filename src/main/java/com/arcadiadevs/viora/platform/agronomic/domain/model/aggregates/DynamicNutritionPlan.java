package com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates;

import com.arcadiadevs.viora.platform.agronomic.domain.exceptions.DynamicNutritionPlanUnavailableException;
import com.arcadiadevs.viora.platform.agronomic.domain.model.events.DynamicNutritionRecommendedEvent;
import com.arcadiadevs.viora.platform.agronomic.domain.model.events.NutritionApplicationCertifiedEvent;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.DynamicNutritionPlanId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplication;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionApplicationWindow;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputRecommendation;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionInputStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.NutritionPlanStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlanRationale;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.UserId;
import com.arcadiadevs.viora.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * DynamicNutritionPlan aggregate root.
 *
 * <p>
 * Represents a nutrition plan generated for a plot in response to a significant
 * climate risk. The plan recommends dosed nutrition inputs to be applied within
 * a specific application window, together with the agronomic rationale behind it.
 * </p>
 */
@Getter
public class DynamicNutritionPlan extends AbstractDomainAggregateRoot<DynamicNutritionPlan> {

    private DynamicNutritionPlanId id;

    private UserId userId;

    private PlotId plotId;

    private NutritionPlanStatus status;

    private List<NutritionInputRecommendation> inputRecommendations;

    private NutritionApplicationWindow applicationWindow;

    private PlanRationale rationale;

    private LocalDate generatedDate;

    /** Executed application; present once the plan has been certified in field. */
    private NutritionApplication application;

    /**
     * Default constructor.
     */
    protected DynamicNutritionPlan() {
        this.inputRecommendations = Collections.emptyList();
    }

    /**
     * Creates a dynamic nutrition plan with an explicit status.
     *
     * <p>
     * Intended for reconstruction from persistence. New plans should be created
     * through {@link #recommend}.
     * </p>
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param status The plan lifecycle status.
     * @param inputRecommendations The recommended nutrition inputs.
     * @param applicationWindow The application window.
     * @param rationale The plan rationale.
     * @param generatedDate The date the plan was generated.
     */
    public DynamicNutritionPlan(
            UserId userId,
            PlotId plotId,
            NutritionPlanStatus status,
            List<NutritionInputRecommendation> inputRecommendations,
            NutritionApplicationWindow applicationWindow,
            PlanRationale rationale,
            LocalDate generatedDate
    ) {
        validateRequiredFields(userId, plotId, status, inputRecommendations, applicationWindow, rationale, generatedDate);
        validateConsistency(inputRecommendations, applicationWindow, generatedDate);

        this.userId = userId;
        this.plotId = plotId;
        this.status = status;
        this.inputRecommendations = List.copyOf(inputRecommendations);
        this.applicationWindow = applicationWindow;
        this.rationale = rationale;
        this.generatedDate = generatedDate;
    }

    /**
     * Recommends a new active dynamic nutrition plan.
     *
     * @param userId The owner user identifier.
     * @param plotId The plot identifier.
     * @param inputRecommendations The recommended nutrition inputs.
     * @param applicationWindow The application window.
     * @param rationale The plan rationale.
     * @param generatedDate The date the plan was generated.
     * @return The recommended active plan.
     */
    public static DynamicNutritionPlan recommend(
            UserId userId,
            PlotId plotId,
            List<NutritionInputRecommendation> inputRecommendations,
            NutritionApplicationWindow applicationWindow,
            PlanRationale rationale,
            LocalDate generatedDate
    ) {
        var plan = new DynamicNutritionPlan(
                userId,
                plotId,
                NutritionPlanStatus.ACTIVE,
                inputRecommendations,
                applicationWindow,
                rationale,
                generatedDate
        );

        plan.registerDomainEvent(new DynamicNutritionRecommendedEvent(
                plan,
                plotId.getValue(),
                userId.getValue(),
                rationale.getTriggeringRiskLevel().name()
        ));

        return plan;
    }

    /**
     * Restores the identity assigned by persistence.
     *
     * @param id The persisted dynamic nutrition plan identifier.
     * @return The identified dynamic nutrition plan.
     */
    public DynamicNutritionPlan restoreIdentity(DynamicNutritionPlanId id) {
        if (id == null) {
            throw new IllegalArgumentException("Dynamic nutrition plan ID is required.");
        }
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Dynamic nutrition plan identity cannot be changed.");
        }

        this.id = id;
        return this;
    }

    /**
     * Marks this plan as superseded by a newer recommendation.
     */
    public void supersede() {
        if (this.status != NutritionPlanStatus.ACTIVE) {
            throw new IllegalStateException("Only an active dynamic nutrition plan can be superseded.");
        }
        this.status = NutritionPlanStatus.SUPERSEDED;
    }

    /**
     * Certifies the in-field execution of this plan.
     *
     * <p>
     * Certification is a sub-state of an active plan: it records the executed
     * application without ending the plan's lifecycle, so the plan remains the
     * plot's current plan and can still be superseded by a newer recommendation.
     * </p>
     *
     * @param application The executed application to record.
     */
    public void certifyApplication(NutritionApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("Nutrition application is required.");
        }
        if (this.status != NutritionPlanStatus.ACTIVE) {
            throw new DynamicNutritionPlanUnavailableException(
                    "Only an active dynamic nutrition plan can be certified.");
        }
        if (this.application != null) {
            throw new DynamicNutritionPlanUnavailableException(
                    "This dynamic nutrition plan has already been certified.");
        }

        var planInputs = inputRecommendations.stream()
                .map(NutritionInputRecommendation::getValue)
                .toList();
        var unknownInput = application.appliedInputs().stream()
                .filter(applied -> !planInputs.contains(applied))
                .findFirst();
        if (unknownInput.isPresent()) {
            throw new IllegalArgumentException(
                    "Applied input '%s' is not part of this plan.".formatted(unknownInput.get()));
        }

        this.application = application;

        registerDomainEvent(new NutritionApplicationCertifiedEvent(
                this,
                plotId.getValue(),
                userId.getValue(),
                application.applicationDate()
        ));
    }

    /**
     * Indicates whether the plan is currently active.
     *
     * @return True when the plan status is ACTIVE.
     */
    public boolean isActive() {
        return this.status == NutritionPlanStatus.ACTIVE;
    }

    /**
     * Indicates whether the plan's application has been certified in field.
     *
     * @return True when an executed application has been recorded.
     */
    public boolean isCertified() {
        return this.application != null;
    }

    /**
     * Restores the executed application during persistence reconstruction.
     *
     * @param application The persisted application.
     * @return This plan.
     */
    public DynamicNutritionPlan restoreApplication(NutritionApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("Nutrition application is required.");
        }
        this.application = application;
        return this;
    }

    private void validateRequiredFields(
            UserId userId,
            PlotId plotId,
            NutritionPlanStatus status,
            List<NutritionInputRecommendation> inputRecommendations,
            NutritionApplicationWindow applicationWindow,
            PlanRationale rationale,
            LocalDate generatedDate
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (plotId == null) {
            throw new IllegalArgumentException("Plot ID is required.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Nutrition plan status is required.");
        }
        if (inputRecommendations == null) {
            throw new IllegalArgumentException("Nutrition input recommendations are required.");
        }
        if (applicationWindow == null) {
            throw new IllegalArgumentException("Application window is required.");
        }
        if (rationale == null) {
            throw new IllegalArgumentException("Plan rationale is required.");
        }
        if (generatedDate == null) {
            throw new IllegalArgumentException("Generated date is required.");
        }
    }

    private void validateConsistency(
            List<NutritionInputRecommendation> inputRecommendations,
            NutritionApplicationWindow applicationWindow,
            LocalDate generatedDate
    ) {
        if (inputRecommendations.isEmpty()) {
            throw new IllegalArgumentException("A dynamic nutrition plan requires at least one input recommendation.");
        }
        var hasRecommendedInput = inputRecommendations.stream()
                .anyMatch(input -> input.getStatus() == NutritionInputStatus.RECOMMENDED);
        if (!hasRecommendedInput) {
            throw new IllegalArgumentException("A dynamic nutrition plan requires at least one RECOMMENDED input.");
        }
        if (applicationWindow.isExpiredOn(generatedDate)) {
            throw new IllegalArgumentException("Application window cannot end before the plan generated date.");
        }
    }
}
