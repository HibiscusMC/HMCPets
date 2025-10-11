package com.hibiscusmc.hmcpets.api.model.registry;

import com.hibiscusmc.hmcpets.api.registry.Registry;
import net.kyori.adventure.key.Key;

public record ActionType(Key key) {

    public static final ActionType MOVES
            = new ActionType(Registry.withDefaultKey("moves"));

    public String id() {
        return key.asString();
    }

}