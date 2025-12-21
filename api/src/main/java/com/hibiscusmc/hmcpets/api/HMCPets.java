package com.hibiscusmc.hmcpets.api;

import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.registry.ActionTypeRegistry;
import com.hibiscusmc.hmcpets.api.registry.CollarTypeRegistry;
import com.hibiscusmc.hmcpets.api.registry.PetRarityRegistry;
import com.hibiscusmc.hmcpets.api.registry.PetTypeRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class HMCPets extends JavaPlugin {

    public final NamespacedKey PET_ID_KEY = new NamespacedKey(this, "pet_id");
    public final NamespacedKey PET_OWNER_KEY = new NamespacedKey(this, "pet_owner");

    @Getter
    private final PetTypeRegistry petTypeRegistry
            = new PetTypeRegistry();
    @Getter
    private final ActionTypeRegistry actionTypeRegistry
            = new ActionTypeRegistry();
    @Getter
    private final CollarTypeRegistry collarTypeRegistry
            = new CollarTypeRegistry();
    @Getter
    private final PetRarityRegistry petRarityRegistry
            = new PetRarityRegistry();

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
        actionTypeRegistry.load();
        collarTypeRegistry.load();
        petRarityRegistry.load();
        petTypeRegistry.load();
    }

    protected abstract void initialize();

    protected abstract void destroy();

    public abstract ILangData langData();

    public static HMCPets getInstance(){
        return instance;
    }
}
