package com.arcadiadevs.viora.platform.surveillance.application.commandservices;

import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.AlertRepository;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreateAlertCommand;
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
            var alertOptional = alertRepository.findById(new com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertId(command.alertId()));
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
}
