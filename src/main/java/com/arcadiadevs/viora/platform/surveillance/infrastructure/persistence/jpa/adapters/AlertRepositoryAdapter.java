package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.AlertRepository;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.AlertAggregateToEntityAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.assemblers.AlertEntityToAggregateAssembler;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlertRepositoryAdapter implements AlertRepository {

    private final SpringDataAlertRepository repository;

    @Override
    public Alert save(Alert alert) {
        var entity = AlertAggregateToEntityAssembler.toEntity(alert);
        var savedEntity = repository.save(entity);
        return AlertEntityToAggregateAssembler.toAggregate(savedEntity);
    }

    @Override
    public Optional<Alert> findById(Long id) {
        return repository.findById(id).map(AlertEntityToAggregateAssembler::toAggregate);
    }

    @Override
    public Optional<Alert> findByReportId(Long reportId) {
        return repository.findFirstByReportIdOrderByIdDesc(reportId)
                .map(AlertEntityToAggregateAssembler::toAggregate);
    }
}
