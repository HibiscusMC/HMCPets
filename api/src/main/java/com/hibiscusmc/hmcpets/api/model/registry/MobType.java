package com.hibiscusmc.hmcpets.api.model.registry;

import com.hibiscusmc.hmcpets.api.i18n.LangEntry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@RequiredArgsConstructor
@Data
@Setter(AccessLevel.NONE)
public abstract class MobType {

    private final Key key;

    @Setter(AccessLevel.PUBLIC)
    private LangEntry name;

    public String id() {
        return key.asString();
    }

    public abstract LivingEntity spawn(String id, Location loc);
    public abstract void addNameplate(Object mobInstance, Component text);
    public abstract void editNameplate(Object mobInstance, Component newText);
    public abstract void removeNameplate(Object mobInstance);
}