package com.arcadiadevs.viora.platform.surveillance.application.commandservices;

import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.AlertRepository;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.ConfirmAlertFromInspectionCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreateAlertCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.DismissReportAlertCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.MarkAlertAsReviewedCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.exceptions.AlertAlreadyReviewedException;
import com.arcadiadevs.viora.platform.surveillance.interfaces.events.AlertReviewedIntegrationEvent;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Command Service for handling Alert-related commands.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertCommandService {

    private final AlertRepository alertRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public Result<Long, ApplicationError> handle(CreateAlertCommand command) {
        log.info("Handling CreateAlertCommand for Plot ID: {}, Type: {}", command.plotId(), command.alertType());
        
        try {
            var alert = new Alert(
                    new PlotId(command.plotId()),
                    command.alertType(),
                    command.severity(),
                    command.title(),
                    command.riskExplanation()
            );

            if (command.sources() != null) {
                command.sources().forEach(alert::addSource);
            }
            if (command.dataProviders() != null) {
                command.dataProviders().forEach(alert::addDataProvider);
            }
            if (command.supportingData() != null) {
                command.supportingData().forEach(alert::addSupportingData);
            }

            // Link the originating report so its inspection lifecycle can later find this alert.
            var reportIdValue = command.supportingData() != null
                    ? command.supportingData().get("Report ID")
                    : null;
            if (reportIdValue != null) {
                try {
                    alert.linkReport(Long.valueOf(reportIdValue));
                } catch (NumberFormatException ignored) {
                    // Non-numeric report id: leave the alert unlinked.
                }
            }

            var savedAlert = alertRepository.save(alert);
            log.info("Alert created successfully with ID: {}", savedAlert.getId().value());

            // Publish integration event
            eventPublisher.publishEvent(new com.arcadiadevs.viora.platform.surveillance.interfaces.events.AlertGeneratedIntegrationEvent(
                    this,
                    savedAlert.getId().value(),
                    savedAlert.getPlotId().value(),
                    savedAlert.getType().name()
            ));

            return Result.success(savedAlert.getId().value());
        } catch (Exception e) {
            log.error("Failed to create alert", e);
            return Result.failure(new ApplicationError("Failed to create alert: ", e.getMessage()));
        }
    }

    public Result<Void, ApplicationError> handle(com.arcadiadevs.viora.platform.surveillance.domain.model.commands.AddAlertTimelineRecordCommand command) {
        log.info("Handling AddAlertTimelineRecordCommand for Alert ID: {}", command.alertId());
        try {
            var alertOptional = alertRepository.findById(command.alertId());
            if (alertOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("alert", command.alertId().toString()));
            }

            var alert = alertOptional.get();
            alert.addTimelineRecord(command.tag(), command.title(), command.description());
            alertRepository.save(alert);
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("Failed to add timeline record", e);
            return Result.failure(new ApplicationError("Failed to add timeline record: ", e.getMessage()));
        }
    }

    /**
     * Confirms (escalates to high priority) the alert raised by a pest sighting report,
     * after the grower's field inspection corroborated the threat. No-op success if the
     * report has no linked alert.
     */
    public Result<Long, ApplicationError> handle(ConfirmAlertFromInspectionCommand command) {
        log.info("Handling ConfirmAlertFromInspectionCommand for Report ID: {}", command.reportId());
        try {
            var alertOptional = alertRepository.findByReportId(command.reportId());
            if (alertOptional.isEmpty()) {
                // 0L signals "no linked alert"; the caller may raise a fresh one.
                return Result.success(0L);
            }

            var alert = alertOptional.get();
            alert.confirmFromInspection();
            var savedAlert = alertRepository.save(alert);
            return Result.success(savedAlert.getId().value());
        } catch (Exception e) {
            log.error("Failed to confirm alert from inspection", e);
            return Result.failure(new ApplicationError("Failed to confirm alert: ", e.getMessage()));
        }
    }

    /**
     * Dismisses the alert raised by a pest sighting report (verified false positive after
     * inspection) so it drops out of the active panel. No-op success if there is no linked alert.
     */
    public Result<Long, ApplicationError> handle(DismissReportAlertCommand command) {
        log.info("Handling DismissReportAlertCommand for Report ID: {}", command.reportId());
        try {
            var alertOptional = alertRepository.findByReportId(command.reportId());
            if (alertOptional.isEmpty()) {
                return Result.success(0L);
            }

            var alert = alertOptional.get();
            alert.dismiss(command.reason());
            var savedAlert = alertRepository.save(alert);
            return Result.success(savedAlert.getId().value());
        } catch (Exception e) {
            log.error("Failed to dismiss report alert", e);
            return Result.failure(new ApplicationError("Failed to dismiss alert: ", e.getMessage()));
        }
    }

    public Result<Long, ApplicationError> handle(MarkAlertAsReviewedCommand command) {
        log.info("Handling MarkAlertAsReviewedCommand for Alert ID: {}", command.alertId());
        try {
            var alertOptional = alertRepository.findById(command.alertId());
            if (alertOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("alert", command.alertId().toString()));
            }

            var alert = alertOptional.get();
            try {
                alert.markAsReviewed();
            } catch (AlertAlreadyReviewedException e) {
                return Result.failure(ApplicationError.validationError("Alert status", "Alert is already reviewed"));
            }
            
            var savedAlert = alertRepository.save(alert);
            log.info("Alert marked as reviewed successfully. ID: {}", savedAlert.getId().value());

            eventPublisher.publishEvent(new AlertReviewedIntegrationEvent(
                    this,
                    savedAlert.getId().value()
            ));

            return Result.success(savedAlert.getId().value());
        } catch (Exception e) {
            log.error("Failed to mark alert as reviewed", e);
            return Result.failure(new ApplicationError("Failed to mark alert as reviewed: ", e.getMessage()));
        }
    }
}
