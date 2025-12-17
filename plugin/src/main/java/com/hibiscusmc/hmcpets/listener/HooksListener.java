package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.config.internal.ConfigService;
import me.lojosho.hibiscuscommons.api.events.HibiscusHooksAllActiveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class HooksListener implements Listener {

    @Inject
    private PetConfig petConfig;
    @Inject
    private MenuConfig menuConfig;
    @Inject
    private HMCPets instance;

    @Inject
    private ConfigService configService;

    @EventHandler
    public void onAllHooksReady(HibiscusHooksAllActiveEvent event) {
        configService.load();
    }

}