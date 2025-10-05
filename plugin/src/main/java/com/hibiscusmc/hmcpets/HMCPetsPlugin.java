package com.hibiscusmc.hmcpets;

import com.hibiscusmc.hmcpets.cache.CacheModule;
import com.hibiscusmc.hmcpets.command.CommandModule;
import com.hibiscusmc.hmcpets.command.CommandService;
import com.hibiscusmc.hmcpets.config.internal.ConfigModule;
import com.hibiscusmc.hmcpets.config.internal.ConfigService;
import com.hibiscusmc.hmcpets.listener.ListenerService;
import com.hibiscusmc.hmcpets.service.ServiceModule;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.storage.StorageService;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Module;

@Log(topic = "HMCPets")
public class HMCPetsPlugin extends JavaPlugin implements Module {

    @Inject
    private ConfigService configService;
    @Inject
    private StorageService storageService;
    @Inject
    private CommandService commandService;
    @Inject
    private ListenerService listenerService;

    @Getter
    private static HMCPetsPlugin instance;

    @Override
    public void onEnable() {
        log.info("-----------------------------------------");
        printBanner();

        long start = System.currentTimeMillis();
        instance = this;

        Injector.create(this).injectMembers(this);

        configService.load();
        storageService.load();
        commandService.load();
        listenerService.load();

        long end = System.currentTimeMillis() - start;
        log.info("HMCPets loaded successfully in " + end + "ms!");
        log.info("-----------------------------------------");
    }

    @Override
    public void onDisable() {
        log.info("-----------------------------------------");
        printBanner();

        long start = System.currentTimeMillis();

        configService.unload();
        storageService.unload();
        commandService.unload();
        listenerService.unload();

        long end = System.currentTimeMillis() - start;
        log.info("HMCPets disabled successfully in " + end + "ms!");
        log.info("-----------------------------------------");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(HMCPetsPlugin.class)
                .toInstance(this);
        binder.bind(JavaPlugin.class).to(HMCPetsPlugin.class);
        binder.bind(Plugin.class).to(HMCPetsPlugin.class);

        binder.install(new ConfigModule(this));
        binder.install(new ServiceModule());
        binder.bind(StorageHolder.class).to(StorageHolder.class);
        binder.install(new CacheModule());
        binder.install(new CommandModule());
    }

    private void printBanner() {
        log.info("   _ _  __ __  ___  ___        _      ");
        log.info("  | | ||  \\  \\|  _>| . \\ ___ _| |_ ___");
        log.info("  |   ||     || <__|  _// ._> | | <_-<");
        log.info("  |_|_||_|_|_|`___/|_|  \\___. |_| /__/");
        log.info("");
        log.info("  HMCPets v" + getPluginMeta().getVersion());
        log.info("  Made by Kiz");
        log.info("");
    }

}