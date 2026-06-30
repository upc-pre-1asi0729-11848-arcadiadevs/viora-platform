package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.TreatmentPrescriptionCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateTreatmentPrescriptionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.LogFieldInspectionDataCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.PrescribeTreatmentCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.TreatmentPrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link TreatmentPrescriptionCommandService}.
 */
@Service
public class TreatmentPrescriptionCommandServiceImpl implements TreatmentPrescriptionCommandService {

    private final TreatmentPrescriptionRepository repository;

    public TreatmentPrescriptionCommandServiceImpl(TreatmentPrescriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Optional<TreatmentPrescription> handle(CreateTreatmentPrescriptionCommand command) {
        // Idempotency check: if it already exists for this proposal, return empty or throw
        if (repository.findByServiceProposalId(command.serviceProposalId()).isPresent()) {
            throw new IllegalArgumentException("A treatment prescription already exists for this service proposal");
        }

        var treatmentPrescription = new TreatmentPrescription(command.serviceProposalId());
        var saved = repository.save(treatmentPrescription);
        return Optional.of(saved);
    }

    @Override
    @Transactional
    public Optional<TreatmentPrescription> handle(LogFieldInspectionDataCommand command) {
        var treatmentOptional = repository.findById(new TreatmentPrescriptionId(command.treatmentPrescriptionId()));
        if (treatmentOptional.isEmpty()) {
            return Optional.empty();
        }

        var treatment = treatmentOptional.get();
        treatment.logFieldInspection(command);
        var saved = repository.save(treatment);
        return Optional.of(saved);
    }

    @Override
    @Transactional
    public Optional<TreatmentPrescription> handle(PrescribeTreatmentCommand command) {
        var treatmentOptional = repository.findById(new TreatmentPrescriptionId(command.treatmentPrescriptionId()));
        if (treatmentOptional.isEmpty()) {
            return Optional.empty();
        }

        var treatment = treatmentOptional.get();
        treatment.prescribeTreatment(command);
        var saved = repository.save(treatment);
        return Optional.of(saved);
    }
}
