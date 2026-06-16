package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.surveillance.domain.model.entities.SymptomDictionaryItem;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.SymptomResource;

public class SymptomResourceFromEntityAssembler {
    public static SymptomResource toResourceFromEntity(SymptomDictionaryItem entity, String language) {
        boolean isSpanish = language != null && language.toLowerCase().startsWith("es");
        String description = isSpanish && entity.getDescriptionEs() != null 
                             ? entity.getDescriptionEs() 
                             : entity.getDescriptionEn();
        return new SymptomResource(entity.getId(), description);
    }
}
