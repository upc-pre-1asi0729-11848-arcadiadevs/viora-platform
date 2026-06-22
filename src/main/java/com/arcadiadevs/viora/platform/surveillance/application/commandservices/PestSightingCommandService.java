package com.arcadiadevs.viora.platform.surveillance.application.commandservices;

import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.ConfirmAlertFromInspectionCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreatePestSightingReportCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.DismissReportAlertCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.ReviewPestSightingReportCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.events.PestSightingReportEvaluatedEvent;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReportStatus;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReporterUserId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.RiskZone;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptom;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptoms;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.PestSightingReportEntityFromPestSightingReportAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.PestSightingReportFromPestSightingReportEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataPestSightingReportRepository;
import com.arcadiadevs.viora.platform.surveillance.application.internal.outboundservices.acl.ExternalAgronomicService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.services.ThreatInferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PestSightingCommandService {

    private final SpringDataPestSightingReportRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExternalAgronomicService externalAgronomicService;
    private final ThreatInferenceService threatInferenceService;
    private final AlertCommandService alertCommandService;

    @Transactional
    public Result<PestSightingReport, ApplicationError> handle(CreatePestSightingReportCommand command) {
        var symptomsList = command.symptoms().stream()
                .map(Symptom::new)
                .toList();

        var aggregate = PestSightingReport.registerManualReport(
                new PlotId(command.plotId()),
                new ReporterUserId(command.reporterUserId()),
                RiskZone.valueOf(command.riskZone().toUpperCase()),
                new Symptoms(symptomsList),
                AlertSeverity.valueOf(command.observedSeverity().toUpperCase()),
                command.notes()
        );

        var currentNdvi = externalAgronomicService.fetchCurrentNdviByPlotId(
                command.plotId(), command.reporterUserId()
        ).orElse(null);

        var inferredThreat = threatInferenceService.inferFromSymptoms(new Symptoms(symptomsList));

        aggregate.evaluateBiologicalRisk(currentNdvi, inferredThreat);

        var entity = PestSightingReportEntityFromPestSightingReportAssembler.toEntityFromAggregate(aggregate);
        try {
            var savedEntity = repository.save(entity);
            var savedAggregate = PestSightingReportFromPestSightingReportEntityAssembler.toAggregateFromEntity(savedEntity);
            
            eventPublisher.publishEvent(new PestSightingReportEvaluatedEvent(
                    this,
                    savedAggregate.getId().value(),
                    savedAggregate.getPlotId().value(),
                    savedAggregate.getReporterUserId().value(),
                    savedAggregate.getCalculatedRisk().name(),
                    savedAggregate.getProbableThreat().name(),
                    savedAggregate.isAlertConfirmed(),
                    savedAggregate.getStatus().name()
            ));
            
            return Result.success(savedAggregate);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            log.error("Validation error handling CreatePestSightingReportCommand", exception);
            return Result.failure(ApplicationError.validationError("pestSightingReport", exception.getMessage()));
        } catch (Exception exception) {
            log.error("Error handling CreatePestSightingReportCommand", exception);
            return Result.failure(ApplicationError.unexpected("pestSightingReport", "An unexpected error occurred"));
        }
    }

    /**
     * Resolves a report after a field inspection: confirms the threat (escalating to a
     * high-priority alert) or rules it out as a verified false positive.
     */
    @Transactional
    public Result<PestSightingReport, ApplicationError> handle(ReviewPestSightingReportCommand command) {
        var entityOptional = repository.findById(command.reportId());
        if (entityOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("pestSightingReport", String.valueOf(command.reportId())));
        }

        var aggregate = PestSightingReportFromPestSightingReportEntityAssembler
                .toAggregateFromEntity(entityOptional.get());

        // Only the reporter who owns the report can resolve it.
        if (!aggregate.getReporterUserId().value().equals(command.reporterUserId())) {
            return Result.failure(ApplicationError.validationError(
                    "reporterUserId", "Report does not belong to the requesting user."));
        }

        ReportStatus outcome;
        try {
            outcome = ReportStatus.valueOf(command.outcome().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            return Result.failure(ApplicationError.validationError(
                    "outcome", "Outcome must be CONFIRMED or RULED_OUT."));
        }

        try {
            switch (outcome) {
                case CONFIRMED -> aggregate.confirmAfterInspection();
                case RULED_OUT -> aggregate.dismissAfterInspection();
                default -> {
                    return Result.failure(ApplicationError.validationError(
                            "outcome", "Outcome must be CONFIRMED or RULED_OUT."));
                }
            }

            var savedEntity = repository.save(
                    PestSightingReportEntityFromPestSightingReportAssembler.toEntityFromAggregate(aggregate));
            var savedAggregate = PestSightingReportFromPestSightingReportEntityAssembler
                    .toAggregateFromEntity(savedEntity);

            // Mirror the resolution onto the alert raised when the report needed inspection:
            // confirming escalates it to high priority; ruling out dismisses it from the panel.
            if (savedAggregate.getStatus() == ReportStatus.CONFIRMED) {
                var confirmResult = alertCommandService.handle(
                        new ConfirmAlertFromInspectionCommand(savedAggregate.getId().value()));

                // No linked alert (e.g. a legacy report that predates the triage flow):
                // raise a fresh high-priority alert through the evaluated-event path.
                // The confirm handler returns 0L when no alert was linked.
                boolean noLinkedAlert = !confirmResult.isFailure()
                        && confirmResult.getOrElse(0L) == 0L;
                if (noLinkedAlert) {
                    eventPublisher.publishEvent(new PestSightingReportEvaluatedEvent(
                            this,
                            savedAggregate.getId().value(),
                            savedAggregate.getPlotId().value(),
                            savedAggregate.getReporterUserId().value(),
                            savedAggregate.getCalculatedRisk().name(),
                            savedAggregate.getProbableThreat().name(),
                            savedAggregate.isAlertConfirmed(),
                            savedAggregate.getStatus().name()
                    ));
                }
            } else if (savedAggregate.getStatus() == ReportStatus.RULED_OUT) {
                alertCommandService.handle(new DismissReportAlertCommand(
                        savedAggregate.getId().value(),
                        "The grower ruled this out as a verified false positive after a field inspection."));
            }

            return Result.success(savedAggregate);
        } catch (IllegalStateException exception) {
            log.error("Invalid state transition handling ReviewPestSightingReportCommand", exception);
            return Result.failure(ApplicationError.validationError("pestSightingReport", exception.getMessage()));
        } catch (Exception exception) {
            log.error("Error handling ReviewPestSightingReportCommand", exception);
            return Result.failure(ApplicationError.unexpected("pestSightingReport", "An unexpected error occurred"));
        }
    }
}
