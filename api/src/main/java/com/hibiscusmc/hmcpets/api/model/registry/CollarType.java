package com.hibiscusmc.hmcpets.api.model.registry;

import lombok.Getter;

@Getter
public enum CollarType {

    BUFF("collarsTypeBuff"),
    HEAL("collarsTypeHeal"),
    SHIELD("collarsTypeShield"),
    ABILITY("collarsTypeAbility"),;

    private final String id;

    CollarType(String id) {
        this.id = id;
    }

    public static CollarType of(String rarity) {
        try {
            return valueOf(rarity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}