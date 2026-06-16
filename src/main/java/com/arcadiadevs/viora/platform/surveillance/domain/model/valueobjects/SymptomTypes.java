package com.arcadiadevs.viora.platform.surveillance.domain.model.valueobjects;

import lombok.Getter;

@Getter
public enum SymptomTypes {
    XYLELLA("XYLELLA", "Xylella fastidiosa symptoms", "Síntomas de Xylella fastidiosa"),
    OLIVE_FLY("OLIVE_FLY", "Olive Fruit Fly damage", "Daño por Mosca del Olivo"),
    WATER_STRESS("WATER_STRESS", "Signs of severe water stress", "Signos de estrés hídrico severo"),
    CHILL_DEFICIT("CHILL_DEFICIT", "Irregular flowering due to chill deficit", "Floración irregular por déficit de frío"),
    CLIMATE_EXTREME("CLIMATE_EXTREME", "Frost or extreme heat damage", "Daño por helada o calor extremo"),
    PEACOCK_SPOT("PEACOCK_SPOT", "Peacock spot fungus symptoms", "Síntomas del hongo repilo"),
    OLIVE_MOTH("OLIVE_MOTH", "Olive moth larvae damage", "Daño por larvas de polilla del olivo");

    private final String code;
    private final String descriptionEn;
    private final String descriptionEs;

    SymptomTypes(String code, String descriptionEn, String descriptionEs) {
        this.code = code;
        this.descriptionEn = descriptionEn;
        this.descriptionEs = descriptionEs;
    }
}
