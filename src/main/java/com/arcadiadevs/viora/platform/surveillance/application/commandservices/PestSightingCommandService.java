package com.arcadiadevs.viora.platform.surveillance.application.commandservices;

import com.arcadiadevs.viora.platform.shared.application.result.ApplicationError;
import com.arcadiadevs.viora.platform.shared.application.result.Result;
import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.PestSightingReport;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.CreatePestSightingReportCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.AlertSeverity;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.PlotId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ReporterUserId;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.RiskZone;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.Symptoms;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.ThreatType;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.PestSightingReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PestSightingCommandService {

    private final PestSightingReportRepository pestSightingReportRepository;

    @Transactional
    public Result<PestSightingReport, ApplicationError> handle(CreatePestSightingReportCommand command) {
        try {
            var plotId = new PlotId(command.plotId());
            var reporterUserId = new ReporterUserId(command.reporterUserId());
            var riskZone = RiskZone.valueOf(command.riskZone());
            var symptoms = Symptoms.fromDescriptions(command.symptoms());
            var observedSeverity = AlertSeverity.valueOf(command.observedSeverity());

            var report = PestSightingReport.registerManualReport(
                    plotId,
                    reporterUserId,
                    riskZone,
                    symptoms,
                    observedSeverity,
                    command.notes()
            );

            // Dummy Risk Evaluation Logic for MVP
            // If observed severity is high or critical, system confirms high risk.
            if (observedSeverity == AlertSeverity.HIGH || observedSeverity == AlertSeverity.CRITICAL) {
                report.evaluateBiologicalRisk(AlertSeverity.HIGH, ThreatType.XYLELLA_RELATED);
                // Here we would potentially raise an event to create an Alert
            } else {
                report.evaluateBiologicalRisk(AlertSeverity.LOW, ThreatType.UNKNOWN);
            }

            var savedReport = pestSightingReportRepository.save(report);

            return Result.success(savedReport);

        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError("pestSightingReport", exception.getMessage()));
        } catch (Exception exception) {
            log.error("Error handling CreatePestSightingReportCommand", exception);
            return Result.failure(ApplicationError.internal("pestSightingReport", "An unexpected error occurred"));
        }
    }
}
