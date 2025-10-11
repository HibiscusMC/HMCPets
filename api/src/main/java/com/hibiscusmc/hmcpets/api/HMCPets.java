package com.hibiscusmc.hmcpets.api;

import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.registry.ActionTypeRegistry;
import com.hibiscusmc.hmcpets.api.registry.PetTypeRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class HMCPets extends JavaPlugin {

    @Getter
    private final PetTypeRegistry petTypeRegistry
            = new PetTypeRegistry();
    @Getter
    private final ActionTypeRegistry actionTypeRegistry
            = new ActionTypeRegistry();

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private static HMCPets instance;

    @Override
    public void onEnable() {
        this.initialize();
    }

    @Override
    public void onDisable() {
        this.destroy();
    }

    public void loadRegistries() {
        petTypeRegistry.load();
        actionTypeRegistry.load();
    }

    protected abstract void initialize();

    protected abstract void destroy();

    public abstract ILangData langData();

}
