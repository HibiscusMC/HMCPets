package com.hibiscusmc.hmcpets.api.model.enums;

import lombok.Getter;

@Getter
public enum PetType {

    AQUATIC("petsTypeAquatic"),
    BEAST("petsTypeBeast"),
    MAGIC("petsTypeMagic"),
    CRITTER("petsTypeCritter");

    private final String id;

    PetType(String id) {
        this.id = id;
    }

    public static PetType of(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
