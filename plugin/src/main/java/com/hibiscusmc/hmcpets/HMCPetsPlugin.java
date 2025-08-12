package com.hibiscusmc.hmcpets;

import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.command.CommandModule;
import com.hibiscusmc.hmcpets.config.ConfigModule;
import com.hibiscusmc.hmcpets.service.Service;
import com.hibiscusmc.hmcpets.service.ServiceModule;
import com.hibiscusmc.hmcpets.storage.Storage;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Module;

import java.util.Set;

@Log(topic = "HMCPets")
public class HMCPetsPlugin extends JavaPlugin implements Module {
    @Inject
    private Set<Service> services;

    @Getter
    private static HMCPetsPlugin instance;

    @Override
    public void onEnable() {
        log.info("-----------------------------------------");
        printBanner();

        long start = System.currentTimeMillis();
        instance = this;

        Injector.create(this).injectMembers(this);

        for (Service service : services) {
            service.load();
        }

        long end = System.currentTimeMillis() - start;
        log.info("HMCPets loaded successfully in " + end + "ms!");
        log.info("-----------------------------------------");
    }

    @Override
    public void onDisable() {
        log.info("-----------------------------------------");
        printBanner();

        long start = System.currentTimeMillis();

        for (Service service : services) {
            service.unload();
        }

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

        binder.bind(Storage.class).toInstance(new Storage());
        binder.bind(UserCache.class).to(UserCache.class);

        binder.install(new ServiceModule());
        binder.install(new ConfigModule());
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
