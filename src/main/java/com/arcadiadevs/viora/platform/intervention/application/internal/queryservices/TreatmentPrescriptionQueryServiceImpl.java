package com.arcadiadevs.viora.platform.intervention.application.internal.queryservices;

import com.arcadiadevs.viora.platform.intervention.application.queryservices.TreatmentPrescriptionQueryService;
import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.TreatmentPrescription;
import com.arcadiadevs.viora.platform.intervention.domain.model.queries.GetTreatmentPrescriptionByIdQuery;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.TreatmentPrescriptionId;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.TreatmentPrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link TreatmentPrescriptionQueryService}.
 */
@Service
public class TreatmentPrescriptionQueryServiceImpl implements TreatmentPrescriptionQueryService {

    private final TreatmentPrescriptionRepository repository;

    public TreatmentPrescriptionQueryServiceImpl(TreatmentPrescriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TreatmentPrescription> handle(GetTreatmentPrescriptionByIdQuery query) {
        return repository.findById(new TreatmentPrescriptionId(query.treatmentPrescriptionId()));
    }
}
