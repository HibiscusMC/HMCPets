package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.event.PetInteractEvent;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class PetInteractListener implements Listener {

    @Inject
    private MenuConfig menuConfig;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void petInteraction(PetInteractEvent event){
        if(!event.player().getUniqueId().equals(event.pet().owner().uuid())) return;

        menuConfig.myPetMenu().open(event.player(), event.pet());
    }

}
