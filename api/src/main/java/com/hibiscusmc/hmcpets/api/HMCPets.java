package com.hibiscusmc.hmcpets.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class HMCPets extends JavaPlugin {

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

    protected abstract void initialize();

    protected abstract void destroy();

}
