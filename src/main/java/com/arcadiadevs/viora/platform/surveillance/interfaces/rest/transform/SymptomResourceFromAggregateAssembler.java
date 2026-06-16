package com.arcadiadevs.viora.platform.surveillance.interfaces.rest.transform;

import com.arcadiadevs.viora.platform.surveillance.domain.model.aggregates.SymptomDictionaryItem;
import com.arcadiadevs.viora.platform.surveillance.interfaces.rest.resources.SymptomResource;

public class SymptomResourceFromAggregateAssembler {
    public static SymptomResource toResourceFromAggregate(SymptomDictionaryItem aggregate, String language) {
        boolean isSpanish = language != null && language.toLowerCase().startsWith("es");
        String description = isSpanish && aggregate.getDescriptionEs() != null 
                             ? aggregate.getDescriptionEs() 
                             : aggregate.getDescriptionEn();
        return new SymptomResource(aggregate.getId(), description);
    }
}
