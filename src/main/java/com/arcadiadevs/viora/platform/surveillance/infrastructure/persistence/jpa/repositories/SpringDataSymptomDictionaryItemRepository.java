package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.repositories;

import com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities.SymptomDictionaryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataSymptomDictionaryItemRepository extends JpaRepository<SymptomDictionaryItemEntity, String> {
}
