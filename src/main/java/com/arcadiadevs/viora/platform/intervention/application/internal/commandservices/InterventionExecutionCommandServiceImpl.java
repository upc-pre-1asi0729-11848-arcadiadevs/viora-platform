package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.InterventionExecutionCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.InterventionExecution;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CertifyApplicationCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.InterventionExecutionRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.TreatmentPrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InterventionExecutionCommandServiceImpl implements InterventionExecutionCommandService {

    private final InterventionExecutionRepository interventionExecutionRepository;
    private final TreatmentPrescriptionRepository treatmentPrescriptionRepository;

    public InterventionExecutionCommandServiceImpl(InterventionExecutionRepository interventionExecutionRepository, TreatmentPrescriptionRepository treatmentPrescriptionRepository) {
        this.interventionExecutionRepository = interventionExecutionRepository;
        this.treatmentPrescriptionRepository = treatmentPrescriptionRepository;
    }

    @Override
    public Optional<InterventionExecution> handle(CertifyApplicationCommand command) {
        var treatmentPrescriptionId = new TreatmentPrescriptionId(command.treatmentPrescriptionId());
        
        if (treatmentPrescriptionRepository.findById(treatmentPrescriptionId.value()).isEmpty()) {
            throw new IllegalArgumentException("Treatment prescription does not exist");
        }

        if (interventionExecutionRepository.existsByTreatmentPrescriptionId(treatmentPrescriptionId)) {
            throw new IllegalArgumentException("Application is already certified for this prescription");
        }

        var execution = new InterventionExecution(command);
        var savedExecution = interventionExecutionRepository.save(execution);
        
        return Optional.of(savedExecution);
    }
}
