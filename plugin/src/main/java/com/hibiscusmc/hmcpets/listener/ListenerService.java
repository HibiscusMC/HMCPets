package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.service.Service;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class ListenerService extends Service {

    @Inject
    private Plugin plugin;
    @Inject
    private Injector injector;

    protected ListenerService() {
        super("Listener");
    }

    @Override
    protected void initialize() {
        PluginManager manager = plugin.getServer().getPluginManager();

        manager.registerEvents(injector.getInstance(HooksListener.class), plugin);
        manager.registerEvents(injector.getInstance(PetOwnerDisconnectListener.class), plugin);
        manager.registerEvents(injector.getInstance(PetEntityInteractionListener.class), plugin);
        manager.registerEvents(injector.getInstance(PetInteractListener.class), plugin);
        manager.registerEvents(injector.getInstance(PlayerJoinListener.class), plugin);
    }

    @Override
    protected void cleanup() {

    }
}
