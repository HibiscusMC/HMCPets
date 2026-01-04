package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.event.PetInteractEvent;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import team.unnamed.inject.Inject;

import java.util.Optional;
import java.util.UUID;

public class PetEntityInteractionListener implements Listener {

    @Inject
    private UserCache userCache;

    @EventHandler
    public void onEntityInteraction(PlayerInteractAtEntityEvent event){
        if(!event.getRightClicked().getPersistentDataContainer().has(HMCPets.instance().PET_ID_KEY)) return;

        String petID = event.getRightClicked().getPersistentDataContainer().get(HMCPets.instance().PET_ID_KEY, PersistentDataType.STRING);
        String ownerID = event.getRightClicked().getPersistentDataContainer().get(HMCPets.instance().PET_OWNER_KEY, PersistentDataType.STRING);

        UUID ownerUUID = UUID.fromString(ownerID);
        UserModel user = userCache.get(ownerUUID);
        if(user == null) return;

        Optional<PetModel> pet = user.getPet(UUID.fromString(petID));
        if(pet.isEmpty()) return;
        if(!pet.get().isSpawned()){ //Well this is awkward... the ID was manipulated in some way.
            Bukkit.getLogger().severe("Impossible pet ID detected on " + petID);
            return;
        }

        PetInteractEvent petInteractEvent = new PetInteractEvent(event.getPlayer(), pet.get());
        Bukkit.getPluginManager().callEvent(petInteractEvent);
    }
}
