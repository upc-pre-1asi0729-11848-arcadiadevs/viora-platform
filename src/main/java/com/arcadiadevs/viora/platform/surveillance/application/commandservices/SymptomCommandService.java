package com.arcadiadevs.viora.platform.surveillance.application.commandservices;

import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.SeedSymptomsCommand;
import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.SymptomDictionaryItem;
import com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects.SymptomTypes;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.SymptomDictionaryItemEntity;
import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories.SpringDataSymptomDictionaryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SymptomCommandService {

    private final SpringDataSymptomDictionaryItemRepository repository;

    @Transactional
    public void handle(SeedSymptomsCommand command) {
        log.info("Starting symptom seeding process...");
        for (SymptomTypes type : SymptomTypes.values()) {
            if (!repository.existsById(type.getCode())) {
                var entity = new SymptomDictionaryItemEntity(
                        type.getCode(),
                        type.getDescriptionEn(),
                        type.getDescriptionEs()
                );
                repository.save(entity);
                log.info("Seeded symptom: {}", type.getCode());
            }
        }
        log.info("Symptom seeding process completed.");
    }
}
