package com.hibiscusmc.hmcpets.api.model.registry;

import net.kyori.adventure.key.Key;

public record ActionType(Key key) {

    public String id() {
        return key.value();
    }

}