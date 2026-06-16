package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.adapters;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.SymptomDictionaryItem;
import com.arcadiadevs.viora.platform.surveillance.domain.repositories.SymptomDictionaryItemRepository;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataSymptomDictionaryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaSymptomDictionaryItemRepositoryAdapter implements SymptomDictionaryItemRepository {

    private final SpringDataSymptomDictionaryItemRepository springDataRepository;

    @Override
    public List<SymptomDictionaryItem> findAll() {
        return springDataRepository.findAll().stream()
                .map(entity -> new SymptomDictionaryItem(entity.getId(), entity.getDescriptionEn(), entity.getDescriptionEs()))
                .collect(Collectors.toList());
    }
}
