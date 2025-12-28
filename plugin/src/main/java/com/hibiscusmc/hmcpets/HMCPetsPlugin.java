package com.hibiscusmc.hmcpets;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.CacheModule;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.command.CommandService;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.internal.ConfigModule;
import com.hibiscusmc.hmcpets.config.internal.ConfigService;
import com.hibiscusmc.hmcpets.listener.ListenerService;
import com.hibiscusmc.hmcpets.service.ServiceModule;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.storage.StorageService;
import com.hibiscusmc.hmcpets.util.hooks.PlaceholderAPIHook;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Module;

import java.util.concurrent.CompletableFuture;

@Log(topic = "HMCPets")
public class HMCPetsPlugin extends HMCPets implements Module {

    @Inject
    private ConfigService configService;
    @Inject
    private StorageService storageService;
    @Inject
    private ListenerService listenerService;
    @Inject
    private CommandService commandService;

    @Inject
    private LangConfig langConfig;

    @Inject
    private UserCache userCache;

    @Inject
    private Injector injector;

    @Override
    public void initialize() {
        log.info("-----------------------------------------");
        printBanner();

        long start = System.currentTimeMillis();
        instance(this);

        Injector.create(this).injectMembers(this);

        loadRegistries();

        configService.load();
        storageService.load();
        listenerService.load();
        commandService.load();

        injector.getInstance(PlaceholderAPIHook.class).register();

        long end = System.currentTimeMillis() - start;
        log.info("HMCPets loaded successfully in " + end + "ms!");
        log.info("-----------------------------------------");
    }

    @Override
    public void destroy() {
        log.info("-----------------------------------------");
        printBanner();

        long start = System.currentTimeMillis();

        userCache.values().forEach(UserModel::destroyActivePets);

        CompletableFuture.allOf(userCache.keySet().stream().map(user -> userCache.save(user).exceptionally(ex -> {
            log.severe("Failed to save user " + user + ". Error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        })).toArray(CompletableFuture[]::new)).join();

        configService.unload();
        storageService.unload();
        listenerService.unload();

        long end = System.currentTimeMillis() - start;
        log.info("HMCPets disabled successfully in " + end + "ms!");
        log.info("-----------------------------------------");
    }

    @Override
    public ILangData langData() {
        return langConfig;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(HMCPets.class)
                .toInstance(this);
        binder.bind(JavaPlugin.class).to(HMCPets.class);
        binder.bind(Plugin.class).to(HMCPets.class);

        binder.install(new ConfigModule(this));
        binder.install(new ServiceModule());

        binder.bind(StorageHolder.class).to(StorageHolder.class);

        binder.install(new CacheModule());
    }

    private void printBanner() {
        log.info("   _ _  __ __  ___  ___        _      ");
        log.info("  | | ||  \\  \\|  _>| . \\ ___ _| |_ ___");
        log.info("  |   ||     || <__|  _// ._> | | <_-<");
        log.info("  |_|_||_|_|_|`___/|_|  \\___. |_| /__/");
        log.info("");
        log.info("  HMCPets v" + getPluginMeta().getVersion());
        log.info("  © Hibiscus Creative Studios");
        log.info("");
    }

}