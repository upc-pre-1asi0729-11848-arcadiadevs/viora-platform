package com.arcadiadevs.viora.platform.surveillance.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "symptom_dictionary_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SymptomDictionaryItemEntity {
    @Id
    private String id;
    private String descriptionEn;
    private String descriptionEs;
}
