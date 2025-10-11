package com.hibiscusmc.hmcpets.api.model.registry;

import com.hibiscusmc.hmcpets.api.registry.Registry;
import net.kyori.adventure.key.Key;

public record CollarType(Key key) {

    public static final CollarType BUFF
            = new CollarType(Registry.withDefaultKey("buff"));
    public static final CollarType HEAL
            = new CollarType(Registry.withDefaultKey("heal"));
    public static final CollarType SHIELD
            = new CollarType(Registry.withDefaultKey("shield"));
    public static final CollarType ABILITY
            = new CollarType(Registry.withDefaultKey("ability"));

    public String id() {
        return key.asString();
    }

}