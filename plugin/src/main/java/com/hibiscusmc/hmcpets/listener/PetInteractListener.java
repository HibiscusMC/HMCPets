package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.event.PetInteractEvent;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

public class PetInteractListener implements Listener {

    @Inject
    private MenuConfig menuConfig;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void petInteraction(PetInteractEvent event){
        if(!event.player().getUniqueId().equals(event.pet().owner().uuid())) return;

        ItemStack inHand = event.player().getInventory().getItemInMainHand();
        if(!inHand.getType().isEdible()) {
            menuConfig.myPetMenu().open(event.player(), event.pet());
            return;
        }

        if(event.pet().health() >= event.pet().maxHealth()) return;

        inHand.setAmount(inHand.getAmount() - 1);
        event.pet().regainHP(3);
    }

}
