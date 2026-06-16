package com.arcadiadevs.viora.platform.surveillance.domain.repositories;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.SymptomDictionaryItem;
import java.util.List;

public interface SymptomDictionaryItemRepository {
    List<SymptomDictionaryItem> findAll();
}
