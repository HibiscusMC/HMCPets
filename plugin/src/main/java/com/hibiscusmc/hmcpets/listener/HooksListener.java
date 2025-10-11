package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import me.lojosho.hibiscuscommons.api.events.HibiscusHooksAllActiveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class HooksListener implements Listener {

    @Inject
    private MenuConfig menuConfig;
    @Inject
    private HMCPets instance;

    @EventHandler
    public void onAllHooksReady(HibiscusHooksAllActiveEvent event) {
        instance.loadRegistries();

        menuConfig.setup();
    }

}