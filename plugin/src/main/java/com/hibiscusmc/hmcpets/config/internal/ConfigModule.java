package com.hibiscusmc.hmcpets.config.internal;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.util.Files;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.AbstractModule;

@RequiredArgsConstructor
public class ConfigModule extends AbstractModule {

    private final Plugin plugin;

    @Override
    protected void configure() {
        bind(LangConfig.class)
                .toInstance(new LangConfig(Files.findOrCreate(plugin.getDataFolder().getPath(), "lang.yml").toPath()));
        bind(PluginConfig.class)
                .toInstance(new PluginConfig(Files.findOrCreate(plugin.getDataFolder().getPath(), "config.yml").toPath()));

        bind(PetConfig.class).to(PetConfig.class);
        bind(MenuConfig.class).to(MenuConfig.class);
    }

}