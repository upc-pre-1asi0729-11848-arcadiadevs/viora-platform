package com.arcadiadevs.viora.platform.surveillance.application.commandservices;

import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreatePestSightingReportCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.events.PestSightingReportEvaluatedEvent;
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

        aggregate.evaluateBiologicalRisk(AlertSeverity.HIGH, ThreatType.PEST_SYMPTOM);

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
                    savedAggregate.getProbableThreat().name()
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
}
