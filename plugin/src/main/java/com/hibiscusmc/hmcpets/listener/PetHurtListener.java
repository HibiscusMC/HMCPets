package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import team.unnamed.inject.Inject;

import java.util.Optional;
import java.util.UUID;

public class PetHurtListener implements Listener {

    @Inject
    private UserCache userCache;


    @EventHandler
    public void onEntityAttack(EntityDamageEvent event){
        if(!event.getEntity().getPersistentDataContainer().has(HMCPets.instance().PET_ID_KEY)) return;

        String petID = event.getEntity().getPersistentDataContainer().get(HMCPets.instance().PET_ID_KEY, PersistentDataType.STRING);
        String ownerID = event.getEntity().getPersistentDataContainer().get(HMCPets.instance().PET_OWNER_KEY, PersistentDataType.STRING);

        UUID ownerUUID = UUID.fromString(ownerID);
        UserModel user = userCache.get(ownerUUID);
        if(user == null) return;

        Optional<PetModel> pet = user.getPet(UUID.fromString(petID));
        if(pet.isEmpty()) return;
        if(!pet.get().isSpawned()){ //Well this is awkward... the ID was manipulated in some way.
            Bukkit.getLogger().severe("Impossible pet ID detected on " + petID);
            return;
        }

        pet.get().hurt(event.getDamage());
    }

}
