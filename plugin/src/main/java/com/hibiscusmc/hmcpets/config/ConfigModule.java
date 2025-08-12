package com.hibiscusmc.hmcpets.config;

import com.hibiscusmc.hmcpets.gui.MenuConfig;
import com.hibiscusmc.hmcpets.i18n.LangConfig;
import com.hibiscusmc.hmcpets.pet.PetConfig;
import com.hibiscusmc.hmcpets.util.Files;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;
import team.unnamed.inject.Singleton;

import java.io.File;

public class ConfigModule extends AbstractModule {

    public static PetConfig petConfig;
    public static LangConfig langConfig;
    public static MenuConfig menuConfig;
    public static PluginConfig pluginConfig;

    @Provides
    @Singleton
    public PetConfig petConfig(@NotNull Plugin plugin) {
        PetConfig config = new PetConfig(
                new File(plugin.getDataFolder().getPath(), "pets").toPath()
        );

        config.setup();

        return petConfig = config;
    }

    @Provides
    @Singleton
    public LangConfig langConfig(@NotNull Plugin plugin) {
        LangConfig config = new LangConfig(
                Files.findOrCreate(plugin.getDataFolder().getPath(), "lang.yml").toPath()
        );

        config.setup();

        return langConfig = config;
    }

    @Provides
    @Singleton
    public MenuConfig menuConfig(@NotNull Plugin plugin) {
        MenuConfig config = new MenuConfig(
                new File(plugin.getDataFolder().getPath(), "menus").toPath()
        );

        config.setup();

        return menuConfig = config;
    }

    @Provides
    @Singleton
    public PluginConfig pluginConfig(@NotNull Plugin plugin) {
        PluginConfig config = new PluginConfig(
                Files.findOrCreate(plugin.getDataFolder().getPath(), "config.yml").toPath()
        );

        config.setup();

        return pluginConfig = config;
    }
}
