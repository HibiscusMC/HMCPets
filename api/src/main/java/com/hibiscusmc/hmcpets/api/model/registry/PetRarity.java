package com.hibiscusmc.hmcpets.api.model.registry;

import com.hibiscusmc.hmcpets.api.i18n.LangEntry;
import com.hibiscusmc.hmcpets.api.registry.Registry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.kyori.adventure.key.Key;

@Data
@Setter(AccessLevel.NONE)
public class PetRarity {

    private final Key key;

    @Setter(AccessLevel.PUBLIC)
    private LangEntry name;

    public static final PetRarity COMMON
            = new PetRarity(Registry.withDefaultKey("common"));
    public static final PetRarity RARE
            = new PetRarity(Registry.withDefaultKey("rare"));
    public static final PetRarity EPIC
            = new PetRarity(Registry.withDefaultKey("epic"));
    public static final PetRarity LEGENDARY
            = new PetRarity(Registry.withDefaultKey("legendary"));

    public String id() {
        return key.asString();
    }

}