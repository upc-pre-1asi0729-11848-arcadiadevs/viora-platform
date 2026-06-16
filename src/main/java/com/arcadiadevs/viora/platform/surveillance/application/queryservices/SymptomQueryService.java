package com.arcadiadevs.viora.platform.surveillance.application.queryservices;

import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.SymptomDictionaryItem;
import com.arcadiadevs.viora.platform.surveillance.domain.model.queries.GetAllSymptomsQuery;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.SymptomDictionaryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymptomQueryService {

    private final SymptomDictionaryItemRepository repository;

    @Transactional(readOnly = true)
    public List<SymptomDictionaryItem> handle(GetAllSymptomsQuery query) {
        return repository.findAll();
    }
}
