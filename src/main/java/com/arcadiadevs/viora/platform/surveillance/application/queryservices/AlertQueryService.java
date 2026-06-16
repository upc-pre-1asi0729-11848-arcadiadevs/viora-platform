package com.arcadiadevs.viora.platform.surveillance.application.queryservices;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.Alert;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.AlertRepository;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAlertByIdQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertQueryService {

    private final AlertRepository alertRepository;

    public Optional<Alert> handle(GetAlertByIdQuery query) {
        return alertRepository.findById(query.alertId());
    }
}
