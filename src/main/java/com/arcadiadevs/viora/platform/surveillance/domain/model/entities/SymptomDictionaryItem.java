package com.arcadiadevs.viora.platform.surveillance.domain.model.entities;

import lombok.Getter;

@Getter
public class SymptomDictionaryItem {
    private String id;
    private String descriptionEn;
    private String descriptionEs;

    protected SymptomDictionaryItem() {}

    public SymptomDictionaryItem(String id, String descriptionEn, String descriptionEs) {
        this.id = id;
        this.descriptionEn = descriptionEn;
        this.descriptionEs = descriptionEs;
    }
}
