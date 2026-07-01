package com.arcadiadevs.viora.platform.intervention.application.internal.commandservices;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.SimulateInterventionPrescriptionCommandService;
import com.arcadiadevs.viora.platform.intervention.application.commandservices.TreatmentPrescriptionCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.CreateTreatmentPrescriptionCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.LogFieldInspectionDataCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.PrescribeTreatmentCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationMethod;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ApplicationSessions;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.Dosage;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.FindingType;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.IncidenceLevel;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PersonalProtectiveEquipment;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PreHarvestInterval;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.PrescribedProduct;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.ServiceProposalStatus;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SprayVolume;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.ServiceProposalRepository;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.TreatmentPrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SimulateInterventionPrescriptionCommandServiceImpl
        implements SimulateInterventionPrescriptionCommandService {

    private final ServiceProposalRepository serviceProposalRepository;
    private final TreatmentPrescriptionRepository treatmentPrescriptionRepository;
    private final TreatmentPrescriptionCommandService treatmentPrescriptionCommandService;

    public SimulateInterventionPrescriptionCommandServiceImpl(
            ServiceProposalRepository serviceProposalRepository,
            TreatmentPrescriptionRepository treatmentPrescriptionRepository,
            TreatmentPrescriptionCommandService treatmentPrescriptionCommandService) {
        this.serviceProposalRepository = serviceProposalRepository;
        this.treatmentPrescriptionRepository = treatmentPrescriptionRepository;
        this.treatmentPrescriptionCommandService = treatmentPrescriptionCommandService;
    }

    @Override
    @Transactional
    public Optional<TreatmentPrescription> simulateForRequest(Long interventionRequestId) {
        var acceptedProposal = serviceProposalRepository
                .findByInterventionRequestId(interventionRequestId).stream()
                .filter(proposal -> proposal.getStatus() == ServiceProposalStatus.ACCEPTED)
                .findFirst()
                .orElse(null);
        if (acceptedProposal == null) {
            return Optional.empty();
        }

        var proposalId = acceptedProposal.getId().value();

        // Idempotent: if a prescription already exists for this proposal, reuse it.
        var existing = treatmentPrescriptionRepository.findByServiceProposalId(proposalId);
        if (existing.isPresent()) {
            return existing;
        }

        var created = treatmentPrescriptionCommandService
                .handle(new CreateTreatmentPrescriptionCommand(proposalId));
        if (created.isEmpty()) {
            return Optional.empty();
        }

        var prescriptionId = created.get().getId().value();

        treatmentPrescriptionCommandService.handle(new LogFieldInspectionDataCommand(
                prescriptionId,
                FindingType.PHYTOSANITARY,
                IncidenceLevel.MEDIUM,
                "Field inspection confirmed phytosanitary pressure in the affected zones.",
                new Date()
        ));

        return treatmentPrescriptionCommandService.handle(new PrescribeTreatmentCommand(
                prescriptionId,
                ApplicationMethod.FOLIAR,
                new SprayVolume(200, "L/ha"),
                new PreHarvestInterval(7),
                "Targeted phytosanitary treatment for affected zones. Apply the recommended "
                        + "protocol and monitor recovery for 14 days.",
                List.of(PersonalProtectiveEquipment.MASK, PersonalProtectiveEquipment.GLOVES),
                List.of(new PrescribedProduct(
                        "Copper-based fungicide",
                        new Dosage(2.5, "L/ha"),
                        new ApplicationSessions(2),
                        "Apply at first symptoms; repeat after 7 days."
                ))
        ));
    }
}
