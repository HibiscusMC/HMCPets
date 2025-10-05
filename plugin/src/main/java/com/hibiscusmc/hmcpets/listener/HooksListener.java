package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.config.MenuConfig;
import me.lojosho.hibiscuscommons.api.events.HibiscusHooksAllActiveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class HooksListener implements Listener {

    @Inject
    private MenuConfig menuConfig;

    @EventHandler
    public void onAllHooksReady(HibiscusHooksAllActiveEvent event) {
        menuConfig.setup();
    }

}