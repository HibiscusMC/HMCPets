package com.hibiscusmc.hmcpets.api.model.registry;

import lombok.Getter;

@Getter
public enum PetRarity {

    COMMON("petsRarityCommon"),
    RARE("petsRarityRare"),
    EPIC("petsRarityEpic"),
    LEGENDARY("petsRarityLegendary");

    private final String id;

    PetRarity(String id) {
        this.id = id;
    }

    public static PetRarity of(String rarity) {
        try {
            return valueOf(rarity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PetRarity.COMMON;
        }
    }

}